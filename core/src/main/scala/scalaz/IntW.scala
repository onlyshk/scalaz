package scalaz

sealed trait IntW extends PimpedType[Int] {
  import Multiplication._

  def ∏ : IntMultiplication = multiplication(value)

  def ordering: Ordering = if (value < 0) LT else if (value > 0) GT else EQ
}

trait Ints {
  implicit def IntTo(n: Int): IntW = new IntW {
    val value = n
  }
}

object IntW extends Ints
