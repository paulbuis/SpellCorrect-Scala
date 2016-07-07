package spelling;
import scala.io.Source;

object LanguageModel {
    
    private val lexicon : Set[String] = {
        val lexiconLines =  Source.fromFile("words.txt", "utf-8").getLines
        lexiconLines.toSet
    } 
    def inLexicon(word:String): Boolean = {
        lexicon(word);
    }   

    private type OneGramPair = (String, Long)
    private val getOneGramWord : OneGramPair => String = pair => pair._1
    private val getOneGramCount : OneGramPair => Long = pair => pair._2
    private val oneGramPairInLexicon : OneGramPair => Boolean = pair => lexicon(getOneGramWord(pair))
    private val oneGramMap : Map[String, Long] = {
        def splitOneGramLine(line : String): OneGramPair = {
            val t : Array[String] = line.split('\t')
            val opt : Long = t(1).toLong
            (t(0), opt )
        }
        val oneGramLines : Iterator[String] = Source.fromFile("count_1w.txt", "utf-8").getLines
        val oneGramPairs : Iterator[OneGramPair] = oneGramLines.map(splitOneGramLine)
        oneGramPairs.filter(oneGramPairInLexicon).toMap
        //oneGramPairs.toMap
    }
    private val oneGramFrequencySum : Long = oneGramMap.map(getOneGramCount).sum
   
    def oneGramFrequency(word: String) : Long = {
        oneGramMap.getOrElse(word, 0L);
    }
    
    val oneGramFrequencyMinThreshold : Long = 100000L
    val oneGramMinProbability : Float = oneGramFrequencyMinThreshold.toFloat / oneGramFrequencySum / 2.0.toFloat

    def isWord(word:String): Boolean = {
        // inLexicon(word) && 
        (oneGramFrequency(word) > oneGramFrequencyMinThreshold)
    }

    def oneGramProbability(word:String): Float = {
        val frequency = oneGramFrequency(word)
        if (frequency > oneGramFrequencyMinThreshold)
            frequency.toFloat / oneGramFrequencySum
        else
            oneGramMinProbability
    }
   
   
   
    private type TwoGramTriple = (String, String, Long)
    private val getTwoGramCount : TwoGramTriple => Long = triple => triple._3
    private val getTwoGramFirstWord : TwoGramTriple => String = triple => triple._1
    private val getTwoGramSecondWord : TwoGramTriple => String = triple => triple._2
    private val twoGramBothInLexicon : TwoGramTriple => Boolean =
        triple => lexicon(getTwoGramFirstWord(triple)) && lexicon(getTwoGramSecondWord(triple)) 
    private lazy val twoGramList : List[TwoGramTriple] = {
        def splitTwoGramLine(line : String) : TwoGramTriple = {
            val t : Array[String] = line.split('\t')
            val opt : Long = t(1).toLong
            val s : Array[String] = t(0).split(' ')
            (s(0), s(1), opt )
        }
        val twoGramLines = Source.fromFile("count_2w.txt", "utf-8").getLines
        val twoGramTriples : Iterator[TwoGramTriple] = twoGramLines.map(splitTwoGramLine)
        twoGramTriples.filter(twoGramBothInLexicon).toList
    }

    private lazy val twoGramFrequencySum : Long = twoGramList.map(getTwoGramCount).sum
    private lazy val twoGramFrequencyMin : Long = twoGramList.map(getTwoGramCount).min
    private type TwoGramTripleSeq = Seq[TwoGramTriple]

    private val simplify2 : TwoGramTripleSeq => Long = seq => getTwoGramCount(seq.head)
    private val simplify1 : TwoGramTripleSeq => Map[String, Long] = 
        value => value.groupBy(getTwoGramSecondWord).mapValues(simplify2)

    private lazy val twoGramMapMap1 : Map[String, Map[String, Long]] =
        twoGramList.groupBy(getTwoGramFirstWord).mapValues(simplify1)


    def twoGramFrequency(word: String, nextWord: String) : Long = {
        twoGramMapMap1.get(word) match {
            case Some(m) => m.get(nextWord) match {
                case Some(frequency) => frequency
                case None => 0L
            }
            case None => 0L
        }
    }

    
    def twoGramProbability(word: String, nextWord: String) : Float = {
        twoGramFrequency(word, nextWord).toFloat / twoGramFrequencySum;
    }
    
    def main(args: Array[String]) { 
        def jumble(s:String):List[String] = s.permutations.filter(LanguageModel.isWord(_)).toList;
        val solutionList :List[(String, Float)] =  jumble("aet").map(x=> (x, LanguageModel.oneGramProbability(x)) ).sortWith((pair1,pair2)=>pair1._2 < pair2._2);
        println(solutionList);

        println(LanguageModel.isWord("payphone"));
        println(LanguageModel.oneGramFrequency("payphone"));

        println(LanguageModel.twoGramProbability("at", "risk"));
        println(LanguageModel.twoGramProbability("012", "abc"));
    }
}