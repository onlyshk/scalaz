package scalaz

trait CoMonad[F[_]] {
  val coBind: CoBind[F]
  val coPointed: CoPointed[F]
  val functor: Functor[F]
  val coJoin: CoJoin[F]

  def extend: Extend[F] = new Extend[F] {
    val functor = CoMonad.this.functor
    val coJoin = CoMonad.this.coJoin
  }

  def coPointedFunctor: CoPointedFunctor[F] = new CoPointedFunctor[F] {
    val functor = CoMonad.this.functor
    val coPointed = CoMonad.this.coPointed
  }

  def cobd[A, B](f: F[A] => B): F[A] => F[B] =
    coBind.coBind(f)

  def coPoint[A]: F[A] => A =
    coPointed.coPoint[A]

  def fmap[A, B](f: A => B): F[A] => F[B] =
    functor.fmap(f)

  def coJn[A]: F[A] => F[F[A]] =
    coJoin.coJoin[A]
}

object CoMonad extends CoMonads

trait CoMonads {
  def coMonad[F[_]](implicit b: CoBind[F], j: CoJoin[F], p: CoPointedFunctor[F]): CoMonad[F] = new CoMonad[F] {
    val coBind = b
    val coPointed = p.coPointed
    val functor = p.functor
    val coJoin = j
  }

  def coMonadEP[F[_]](implicit e: Extend[F], p: CoPointed[F]): CoMonad[F] = new CoMonad[F] {
    val coBind = CoBind.coBind
    val coPointed = p
    val functor = e.functor
    val coJoin = e.coJoin
  }

  def coMonadBP[F[_]](implicit b: CoBind[F], p: CoPointed[F]): CoMonad[F] = new CoMonad[F] {
    val coBind = b
    val coPointed = p
    val functor = new Functor[F] {
      def fmap[A, B](f: A => B): F[A] => F[B] =
        b.coBind(f compose p.coPoint)
    }
    val coJoin = new CoJoin[F] {
      def coJoin[A] =
        b.coBind(identity[F[A]])
    }
  }

  def coMonadJP[F[_]](implicit j: CoJoin[F], p: CoPointedFunctor[F]): CoMonad[F] = new CoMonad[F] {
    val coBind = new CoBind[F] {
      def coBind[A, B](f: F[A] => B): F[A] => F[B] =
        p.fmap(f) compose j.coJoin
    }
    val coPointed = p.coPointed
    val functor = p.functor
    val coJoin = j
  }
}