package services

import scala.io.Source

class DictionarySearcher {

  private val words = (for (line <- Source.fromFile("conf/english3.txt").getLines()) yield line).toSeq

  def wordsExistsThatStartWith(prefix: String): Boolean = words.exists(_.startsWith(prefix))

  def isWord(word: String): Boolean = words.contains(word)

}
