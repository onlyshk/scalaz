package scalaz

sealed trait CharW extends PimpedType[Char] {
  import Multiplication._
  import Digit._
  import Alpha._

  def ∏ : CharMultiplication = multiplication(value)

  def digit : Option[Digit] = digits find (_.toChar == value)

  def alpha : Option[Alpha] = alphas find(_.toChar == value)
}

trait Chars {
  implicit def CharTo(c: Char): CharW = new CharW {
    val value = c
  }
}

object CharW extends Chars