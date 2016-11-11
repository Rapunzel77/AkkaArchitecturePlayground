import org.junit.Test

/**
  * @author arno
  */
class MyFirstTest {
  @Test
  def testWhatever(): Unit = {}

  @Test
  def testname(): Unit = {
    assert(X.name == "ich")
  }

}
