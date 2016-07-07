package spelling


object CandidateEvaluator1 {
    type EvaluationDetails = List[(String, Double)] // ( reason, probability) pairs
    def getEvaluationDetailReason(detail  : (String, Double)) : String = detail._1
    def getEvaluationDetailProbability(detail : (String, Double)) : Double = detail._2
    
    type EvaluatedCandidate = (String, EvaluationDetails, Double)
    def getEvaluatedProbability(eCandidate : EvaluatedCandidate) : Double = eCandidate._3
    def getEvaluatedDetail (eCandidate: EvaluatedCandidate) : EvaluationDetails = eCandidate._2
    def getEvaluatedWord (eCandidate: EvaluatedCandidate) : String = eCandidate._1
    
    private def compareEvaluated (ec1: EvaluatedCandidate, ec2: EvaluatedCandidate) : Boolean = 
        getEvaluatedProbability(ec1) > getEvaluatedProbability(ec2)
   
    private def evaluateEditList(e : Iterable[String]) : Double = 
            e.map(ErrorModel.editProbability).product
                 
    def evaluateCandidates(candidates : CandidateGenerator.CandidateList) : List[EvaluatedCandidate] = {
        val evaluatedCandidates : Iterable[EvaluatedCandidate] = for ((word, editListAlternates) <- candidates) yield {
            val evaluatedAlternates : Double =
                if (editListAlternates.isEmpty) 1.0
                else editListAlternates.map(evaluateEditList).max 
            val evaluationMap : EvaluationDetails =  List(
                ("wordProbability" , LanguageModel.oneGramProbability(word)),
                ("editProbability" , evaluatedAlternates*evaluatedAlternates*evaluatedAlternates)
            );
            (word, evaluationMap, evaluationMap.map(getEvaluationDetailProbability).product )
        }
        
        val evaluatedList : List[EvaluatedCandidate] = evaluatedCandidates.toList
        evaluatedList.sortWith(compareEvaluated)
    }
    
    def main(args : Array[String]) = {
        val c : CandidateGenerator.CandidateList = CandidateGenerator.candidates("aet")
        println("\nCandidates:")
        println(c)
        val e : List[EvaluatedCandidate] = evaluateCandidates(c)
        println("\nEvaluated Candidates:")
        println(e)
        val pick : EvaluatedCandidate = e.head
        println("\nBest Pick:")
        println(pick)
        val word : String = getEvaluatedWord(pick)
        println("\nCorrect Word:")
        println(word)
        
    }
}

