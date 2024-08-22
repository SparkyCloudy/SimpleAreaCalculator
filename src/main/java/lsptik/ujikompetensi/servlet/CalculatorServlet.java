package lsptik.ujikompetensi.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;
import lombok.Cleanup;
import lombok.SneakyThrows;

@WebServlet(urlPatterns = "/area-calculation")
public class CalculatorServlet extends HttpServlet {

  @Override
  @SneakyThrows
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
    String html = getMainHtml();

    if (req.getCookies() != null) {
      for (var cookie : req.getCookies()) {
        if (cookie.getName().equals("result-value")) {
          html = html.replace("<!-- Result -->", cookie.getValue());
        }
      }
    }

    // show the final HTML
    resp.getWriter().println(html);
  }

  @Override
  @SneakyThrows
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
    Map<String, String[]> params = req.getParameterMap();
    saveHistory(params);

    resp.addCookie(new Cookie("result-value", calculation(params)));
    resp.sendRedirect("/area-calculation");
  }

  /**
   * Do Shape calculation based on user Shape Selection and value that returned
   *
   * @param params retrived parameters from request
   * @return result of shape area
   */
  @SneakyThrows
  private String calculation(Map<String, String[]> params) {
    DecimalFormat df = new DecimalFormat("#.##");
    String[] shape = params.get("shape-id");
    double value = switch (shape[0]) {
      case "triangle" -> {
        double height = Double.parseDouble(params.get("triangleHeight")[0]);
        double base = Double.parseDouble(params.get("triangleBase")[0]);
        yield (base * height) / 2;
      }

      case "square" -> {
        double side = Double.parseDouble(params.get("squareSide")[0]);
        yield side * side;
      }

      case "circle" -> {
        double radius = Double.parseDouble(params.get("circleRadius")[0]);
        yield Math.PI * radius * radius;
      }
      default -> throw new IllegalStateException("Unexpected value: " + shape[0]);
    };

    return df.format(value);
  }

  @SneakyThrows
  private String getMainHtml() {
    // get UI of HTML
    Path path = Path.of(
        Objects.requireNonNull(CalculatorServlet.class.getResource("/html/main_ui.html")).getPath()
            .substring(1));
    return Files.readString(path);
  }

  /**
   * Save every form input into a CSV file
   *
   * @param params retrived parameters from request
   */
  @SneakyThrows
  private void saveHistory(Map<String, String[]> params) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
    String result = calculation(params);
    StringBuilder szRowData = new StringBuilder();

    String historyUrl = Objects.requireNonNull(
        CalculatorServlet.class.getResource("/data/history" + ".csv")).getPath().substring(1);
    Path path = Path.of(historyUrl);

    szRowData.append(dateFormat.format(Calendar.getInstance().getTime())).append(",");

    for (var entry : params.entrySet()) {
      szRowData.append(entry.getValue()[0]).append(",");
    }

    szRowData.append(result);

    @Cleanup BufferedWriter bufferedWriter = Files.newBufferedWriter(path,
        StandardOpenOption.APPEND);

    bufferedWriter.write(szRowData.toString());
    bufferedWriter.newLine();
  }
}
