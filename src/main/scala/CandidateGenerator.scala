package spelling;


object CandidateGenerator {
    private val alphabet : Seq[Char] = 'a' to 'z';

    type WordEditPair = (String, String);

    def edits1(word: String) : Iterable[WordEditPair] = {

        val splits : Seq [ (String, String) ] = 
            for(i <- 0 to word.length) yield {
                (word take i, word drop i)
            }

        val deletes : Seq[WordEditPair] = 
            for((front, back) <- splits.slice(0, splits.length-1)) yield {
                val previous : String = if (front.length > 0) front.slice(front.length-1,front.length) else "<"
                (front + back.substring(1), previous+back(0) + "|" + previous)
            }
        
        val transposes : Seq[WordEditPair] = 
            for((front, back) <- splits.slice(0, splits.length-2);
                if (back.substring(0,1) != back.substring(1,2)) ) yield {
                    val back0 : String = back.substring(0,1)
                    val back1 : String = back.substring(1,2)
                    val back10 : String = back1 + back0
                    (front + back10 + back.substring(2), back0+back1 + "|" + back10 )
            }

        val replaces : Seq[WordEditPair] = 
            for((front, back) <- splits.slice(0, splits.length-1); alpha <- alphabet;
                if alpha != back(0) ) yield {
                    (front + alpha + back.substring(1), back(0) + "|" + alpha)
            }
        
        val inserts : Seq[WordEditPair] = 
            for((front, back) <- splits; alpha <- alphabet) yield {
                val previous : String = if (front.length > 0) front.slice(front.length - 1, front.length) else "<"
                (front + alpha + back, previous + "|" + previous + alpha)
            }
    
        deletes ++ transposes ++ replaces ++ inserts
    }


    private def known(word : String) : Boolean = LanguageModel.isWord(word)
    
    private def knownPair(pair: WordEditPair) : Boolean = LanguageModel.isWord(pair._1) 

    def known_edits1(s : String) : Iterable[WordEditPair] = {
        edits1(s).filter(knownPair)
    }

    private type WordEditListPair = (String, List[String]);
    type EditListAlternates = Iterable[List[String]];
    private type CandidateMap = Map[String, EditListAlternates];
    type CandidateList = List[(String, EditListAlternates)];
    
    private def getWord1(pair: WordEditPair) : String =  pair._1
    private def getEditList1(pair: WordEditPair) :List[String] =  List(pair._2)
    private def simplify1(pairs: Iterable[WordEditPair]) : EditListAlternates = 
        pairs.map(getEditList1)
    
    private def getWord2(pair: WordEditListPair) : String = pair._1
    private def getEditList2(pair: WordEditListPair)  : List[String] = pair._2
    private def simplify2(pairs: Iterable[WordEditListPair]) : EditListAlternates = 
        pairs.map(getEditList2)

    private def known_edits2(s : String) : Iterable[WordEditListPair] = {
        val e1 = edits1(s)
        val e1Set = e1.map(getWord1).toSet
        for (e <- e1; e2 <- edits1(getWord1(e));
             if known(getWord1(e2)) && ! e1Set.contains(getWord1(e2)) && !getWord1(e2).equals(s) ) yield  
                (e2._1, List(e._2, e2._2))
    }
    
    private def known_edits3(s: String) : Iterable[WordEditListPair] = {
        val e1 = edits1(s)
        for (e <- e1; e2 <- edits1(getWord1(e)); e3 <- edits1(getWord1(e2));
            if known(getWord1(e3)) ) yield
            (e3._1, List(e._2, e2._2, e3._2))
    }


    def candidates(s:String) : CandidateList = {
        val isWord = known(s);

        val d0 : CandidateList = if (isWord) 
                List ((s, List.empty))
            else 
                List.empty
             
        val d1 : CandidateList = {
            val wordEdits : Iterable[WordEditPair] = known_edits1(s)
            val wordGrouped : Map[String, Iterable[WordEditPair]] = wordEdits.groupBy(getWord1) 
            val wordGroupedSimplified : CandidateMap = wordGrouped.mapValues(simplify1)
            wordGroupedSimplified.toList
        }

        if (isWord || s.length < 5) 
            d0 ++ d1
        else {
            val d2 : CandidateList = {
                val wordEdits : Iterable[ WordEditListPair ] = known_edits2(s)
                val wordGrouped : Map[ String, Iterable[WordEditListPair] ] = wordEdits.groupBy(getWord2)
                val wordGroupedSimplified : CandidateMap = wordGrouped.mapValues(simplify2)
                wordGroupedSimplified.toList
            }
            d1 ++ d2
        }
    }
}