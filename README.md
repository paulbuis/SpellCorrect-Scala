# SpellCorrect-Scala

##A Peter Norvig inspired spelling corrector in Scala

Inspired by Peter Norvig's [_How to Write a Spelling Corrector_](http://norvig.com/spell-correct.html) and
[his chapter in _Natural Lanauge Corpus Data: Beautiful Data_](http://norvig.com/ngrams/)

This project is intended to be compiled and run by `sbt` (The Scala Build Tool). See http://www.scala-sbt.org/ for
full documentation on `sbt`

In the root directory of the project run the command
```bash
sbt
```

The first time you run `sbt` it will likely download and install additional components.

`sbt` provides an interactive command prompt.

To compile the project use the command `compile` at the prompt. Again, the first time you do this some
one-time configuration tasks will run.

To run the `main()` function out of one of the compiled classes use the `run` command. It will provide you
with a numbered list of classes that supply a `main()` function. In this project, you should expect to
see something like:
```
Multiple main classes detected, select one to run:

 [1] Testing
 [2] spelling.CandidateEvaluator1
 [3] spelling.ErrorModel
 [4] spelling.LanguageModel
Enter number:
```

This list includes 3 classes in the `spelling` package and the `Testing` class in the anonymous package.

The current code generates 83.7% correct results which is better than Norvig got out of his
original Python program (which gets 74% correct). This code uses a different language model based on the data supplied with
his book chapter in _Beautiful Data_. It also incorporates an error model. Correctness improvements
based on the error model have not yet been measured. It is likely that the error model as implemented
is badly flawed. No attempt has been made to optimze this for speed. Norvig's goal is to be able to
run the test case (without logging output) in 27 seconds.

For more detail on spelling correction and to see how the error model is supposed to work see lectures by
Dan Jurafsky of Stanford on Natural Language Processing
*[_Spelling Correction and the Noisy Channel_ (PDF slides)](https://web.stanford.edu/class/cs124/lec/spelling.pdf)
*[_Spelling Correction and the Noisy Channel_ Draft of Chapter 6 of _Speech and Lnaguage Processing_](https://web.stanford.edu/~jurafsky/slp3/6.pdf)

YouTube Videos (including transcripts):
*[5 - 1 - The Spelling Correction Task - Stanford NLP - Professor Dan Jurafsky & Chris Manning](https://www.youtube.com/watch?v=Z1m7McLIP9c)
*[5 - 2 - The Noisy Channel Model of Spelling - Stanford NLP - Professor Dan Jurafsky & Chris Manning](https://www.youtube.com/watch?v=RgHr2KVXtiE)
*[5 - 3 - Real-Word Spelling Correction - Stanford NLP - Professor Dan Jurafsky & Chris Manning](https://www.youtube.com/watch?v=AcpGX_fMHEI)
*[5 - 4 - State of the Art Systems - Stanford NLP - Professor Dan Jurafsky & Chris Manning](https://www.youtube.com/watch?v=s7bMicEKmMU)
*[5 - 2 - The Noisy Channel Model of Spelling - Stanford NLP - Professor Dan Jurafsky & Chris Manning (YouTube)](
