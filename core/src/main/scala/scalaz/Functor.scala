package scalaz

import java.util.Map.Entry
import java.util.AbstractMap.SimpleImmutableEntry

trait Functor[F[_]] {
  def fmap[A, B](f: A => B): F[A] => F[B]

  def compose[G[_]](gtr: Functor[G]): Functor[({type λ[α] = F[G[α]]})#λ] = new Functor[({type λ[α] = F[G[α]]})#λ] {
    def fmap[A, B](f: A => B): F[G[A]] => F[G[B]] =
      Functor.this.fmap(gtr.fmap(f))
  }
}

object Functor extends Functors

trait Functors {

  import Const._

  implicit def ConstFunctor[B]: Functor[({type λ[α] = Const[B, α]})#λ] = new Functor[({type λ[α] = Const[B, α]})#λ] {
    def fmap[A, X](f: A => X) =
      r => const(r.value)
  }

  implicit def Function1Functor[T]: Functor[({type λ[α] = Function1[T, α]})#λ] = new Functor[({type λ[α] = Function1[T, α]})#λ] {
    def fmap[A, B](f: A => B) = _ andThen f
  }

  implicit val OptionFunctor: Functor[Option] = new Functor[Option] {
    def fmap[A, B](f: A => B) = _ map f
  }

  implicit val ListFunctor: Functor[List] = new Functor[List] {
    def fmap[A, B](f: A => B) = _ map f
  }

  implicit val StreamFunctor: Functor[Stream] = new Functor[Stream] {
    def fmap[A, B](f: A => B) = _ map f
  }

  implicit def MapEntryFunctor[X]: Functor[({type λ[α] = Entry[X, α]})#λ] = new Functor[({type λ[α] = Entry[X, α]})#λ] {
    def fmap[A, B](f: A => B) = r => new SimpleImmutableEntry(r.getKey, f(r.getValue))
  }

}
