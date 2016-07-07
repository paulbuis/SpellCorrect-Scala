package spelling;
import scala.io.Source;

object ErrorModel {
      
    private val editMap : Map[String, Int] = {
        def splitEditLine(line : String): (String, Int) = {
            val t : Array[String] = line.split('\t');
            val opt : Int = t(1).toInt
            (t(0), opt );
        }
        val editLines : Iterator[String] = Source.fromFile("count_1edit.txt", "utf-8").getLines;
        val editPairs : Iterator[(String, Int)] = editLines.map(splitEditLine);
        editPairs.toMap
    }
     
    private val editFrequencySum : Int = editMap.values.sum;
    
    def editFrequency(edit: String) : Int = {
        editMap.getOrElse(edit, 1);
    }
    
    def editProbability(edit:String): Float = {
        editFrequency(edit).toFloat / editFrequencySum;
    }  
    def main(args: Array[String]) {
          println(ErrorModel.editFrequencySum) 
          println(ErrorModel.editFrequency("ie|ei"));
          println(ErrorModel.editFrequency("ei|ie"));
          println(ErrorModel.editFrequency("zz|z"));
    }
}