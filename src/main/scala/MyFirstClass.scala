import scala.annotation.tailrec
import scala.util.control.NonFatal

object MyFirstClass extends App {
  println("Hallo Nico und Arno")
  println(System.currentTimeMillis())
  Thread.sleep(1000)
  println(X.name)
  X.name.foreach( println )
  val p  = Plane("NicoAir", "Moon")
  val p2 = Plane(name= "ArnoAir")

  val PI = 3.14


  def doit (o:Any) = o match {
    case 1 => "eins"
    case "2" => "zwei"
    case ThisOrThat("abc") => "ABC"
    case ThisOrThat(n) => n
    case Plane(n, "Haasenbau") => "Wald"
    case Plane(n,d) => n+":"+d
    case PI         => println("na sowas?")
  }

  println(doit(p))
  println(doit(p2))
  println(doit(ThisOrThat("abc")))
  println(doit(1))

  var aMap=Map[Int, String]()
  aMap += 1 -> "Eins"
  aMap.get(1).foreach(println)
  aMap.get(3) match {
    case Some (s) => println(s)
    case None     => println("Nothing there")
  }

  @volatile var aList = Vector[String]()
  aList :+= "TaTa"

  def a:Nothing = throw new IllegalArgumentException

  def b:Int = ???

  def plus (i:Int)(j:Int) = i+j

  plus(1)(2)

  val plus1 = plus(1)_

  println(plus1(5))

  def fact(n: Int) = {
    @tailrec def rec (n: Int, res: Int): Int = n match {
      case 1 => res
      case _ => rec (n-1, res*n)
    }
    rec (n, 1)
  }

  println(new D("D").x)

  implicit val c = Y.c1

  X.doWhat ("a")
  X.doWhat ("b")


  a (x => x)
  a (_+1)


  def a (i: Int => Int) = println (i(0))


  println(b (System.currentTimeMillis()))

  try b(9)
  catch {
    case e: NullPointerException if e.getMessage == null => 5
    case NonFatal (e) => 7
    case _ => 8
  }finally {

  }


  def b (i: => Long) = {
    println (i)
    Thread.sleep(5)
    println (i)
  }


  def c (f: Int => Int) = f(9)


  c {
    case 1 => 25
    case 5 => 7
    case x if x>0 => x+1
  }

}

case class Ctx(s: String)

object Y {
  implicit val c1 = Ctx ("1")
  implicit val c2 = Ctx ("2")
}

object X {
  lazy val name2 = "ich"+System.currentTimeMillis()

  def name = {
    name2
  }

  def doWhat(i: Any)(implicit ctx: Ctx): Unit = {
    abc (i, 1)
  }

  def abc (i: Any, a: Int)(implicit ctx: Ctx): Unit = {
    println (s"$i with $a in ctx ${ctx.s}")
  }
}

object Plane {
  def apply(name:String="Airplane", destination:String="Haasenbau") = new Plane(name, destination)
  def unapply(p:Plane):Option[(String, String)] = Some((p.name, p.destination))
}

class Plane (val name:String, val destination:String){
  println(description)
  final val x = 1
  def description:String = s"Creating Plane $name to fly to $destination"

  def copy (name:String=name, destination:String=destination)=Plane(name, destination)
}

final case class ThisOrThat(what:String)

sealed trait A{
  def x = 1
}

trait B extends A{
  override def x = 3

  def a : String
}
trait C extends A{
  override def x = 2
}

class D(override val a: String) extends B with C
