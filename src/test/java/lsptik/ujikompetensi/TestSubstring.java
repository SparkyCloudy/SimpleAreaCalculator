package lsptik.ujikompetensi;

import lsptik.ujikompetensi.servlet.CalculatorServlet;
import org.junit.jupiter.api.Test;

public class TestSubstring {

  @Test
  void testPath() {
    System.out.println(CalculatorServlet.class.getResource("/html/main_ui.html").getPath().substring(1));

  }
}
