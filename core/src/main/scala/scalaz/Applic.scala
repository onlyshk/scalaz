package scalaz

trait Applic[F[_]] {
  def applic[A, B](f: F[A => B]): F[A] => F[B]
}

object Applic extends Applics

trait Applics {

  import Const._

  implicit def ConstApplic[B: Semigroup]: Applic[({type λ[α] = Const[B, α]})#λ] = new Applic[({type λ[α] = Const[B, α]})#λ] {
    def applic[A, X](f: Const[B, A => X]) =
      fa =>
        const[X](implicitly[Semigroup[B]].append(f.value, fa.value))
  }

  implicit val OptionApplic: Applic[Option] = new Applic[Option] {
    def applic[A, B](f: Option[A => B]) =
      a => f flatMap (a map _)
  }

  implicit val ListApplic: Applic[List] = new Applic[List] {
    def applic[A, B](f: List[A => B]) =
      a => f flatMap (a map _)
  }

  implicit val StreamApplic: Applic[Stream] = new Applic[Stream] {
    def applic[A, B](f: Stream[A => B]) =
      a => f flatMap (a map _)
  }

  implicit def Function1Applic[T]: Applic[({type λ[α] = Function1[T, α]})#λ] = new Applic[({type λ[α] = Function1[T, α]})#λ] {
    def applic[A, B](f: T => A => B) =
      g => x => f(x)(g(x))
  }
}