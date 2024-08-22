package lsptik.ujikompetensi.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import lombok.Cleanup;
import lombok.SneakyThrows;

@WebServlet(urlPatterns = "/dashboard")
public class DashboardServlet extends HttpServlet {

  @Override
  @SneakyThrows
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
    String html = getMainHtml().replace("<!-- TOTAL_CALCULATIONS -->", getTotalCalculation())
        .replace("<!-- MAX_CALCULATION -->", getMaxValueOfCalculation())
        .replace("<!-- MIN_CALCULATION -->", getMinValueOfCalculation())
        .replace("<!-- PERCENTAGE_TRIANGLE -->", getPercentageShape("triangle"))
        .replace("<!-- PERCENTAGE_SQUARE -->", getPercentageShape("square"))
        .replace("<!-- PERCENTAGE_CIRCLE -->", getPercentageShape("circle"));

    if (!listOfCalcRecords().isEmpty()) {
      boolean reverseOrder = "true".equals(req.getParameter("reverse"));
      html = html.replace("<!-- List of calculations -->", getListCalcultaionsHtml(reverseOrder));
    }

    resp.getWriter().println(html);
  }

  @SneakyThrows
  private String getListCalcultaionsHtml(boolean reverseOrder) {
    StringBuilder szChatList = new StringBuilder();
    List<String> calculations = listOfCalcRecords();

    if (reverseOrder) {
      Collections.reverse(calculations);
    }

    szChatList.append("<div class=\"max-h-96 overflow-y-auto\">");
    szChatList.append("<ul class=\"list-none pl-5 space-y-4\">");

    for (String calculation : calculations) {
      String[] dataFragments = calculation.split(",");
      szChatList.append("<li class=\"bg-white p-4 rounded-lg shadow-md\">");
      szChatList.append("<span class=\"block font-semibold\">").append(dataFragments[0])
          .append("</span>");
      szChatList.append("<span class=\"block\"><b>Type</b>: ").append(dataFragments[1])
          .append("</span>");
      switch (dataFragments[1]) {
        case "triangle" -> {
          szChatList.append("<span class=\"block\"><b>Base</b>: ").append(dataFragments[2])
              .append("</span>");
          szChatList.append("<span class=\"block\"><b>Height</b>: ").append(dataFragments[3])
              .append("</span>");
        }
        case "square" ->
            szChatList.append("<span class=\"block\"><b>Side</b>: ").append(dataFragments[2])
                .append("</span>");
        case "circle" ->
            szChatList.append("<span class=\"block\"><b>Radius</b>: ").append(dataFragments[2])
                .append("</span>");
      }
      szChatList.append("<span class=\"block\"><b>Value</b>: ")
          .append(dataFragments[dataFragments.length - 1]).append("</span>");
      szChatList.append("</li>");
    }

    szChatList.append("</ul>");
    szChatList.append("</div>");

    return szChatList.toString();
  }

  @SneakyThrows
  private String getMainHtml() {
    // get UI of HTML
    Path path = Path.of(
        Objects.requireNonNull(DashboardServlet.class.getResource("/html/dashboard_ui.html"))
            .getPath().substring(1));
    return Files.readString(path);
  }

  private String getTotalCalculation() {
    List<String> datas = listOfCalcRecords();
    return String.valueOf(datas.size());
  }

  /**
   * Extract calculation values of Records
   *
   * @return List of
   */
  private List<Double> extractValues() {
    List<String> datas = listOfCalcRecords();
    List<Double> values = new ArrayList<>();

    for (String data : datas) {
      String[] dataFragments = data.split(",");
      values.add(Double.parseDouble(dataFragments[dataFragments.length - 1]));
    }

    return values;
  }

  /**
   * Count the shape type
   *
   * @param shape shape that want to counted
   * @return the number of shape
   */
  private int countShapeTypes(String shape) {
    List<String> datas = listOfCalcRecords();
    int count = 0;

    for (String data : datas) {
      String[] dataFragments = data.split(",");
      if (dataFragments[1].equals(shape)) {
        count++;
      }
    }

    return count;
  }

  private String getPercentageShape(String shape) {
    DecimalFormat df = new DecimalFormat("#.##");
    int total = listOfCalcRecords().size();
    int shapeCount = countShapeTypes(shape);

    return df.format((shapeCount / (double) total) * 100);
  }

  private String getMaxValueOfCalculation() {
    List<Double> values = extractValues();
    return String.valueOf(Collections.max(values));
  }

  private String getMinValueOfCalculation() {
    List<Double> values = extractValues();
    // get minimum value from list
    return String.valueOf(Collections.min(values));
  }

  /**
   * Get list of Calculation Records from CSV file
   *
   * @return List of Calculations
   */
  @SneakyThrows
  private List<String> listOfCalcRecords() {
    /*
    Ini komentar paragraf pertama,
    Ini komentar paragraf kedua
     */
    Path path = Path.of(
        Objects.requireNonNull(DashboardServlet.class.getResource("/data/history.csv")).getPath()
            .substring(1));
    List<String> historyCalcDatas = new ArrayList<>();

    @Cleanup Scanner sc = new Scanner(path);
    while (sc.hasNextLine()) {
      historyCalcDatas.add(sc.nextLine());
    }

    return historyCalcDatas;
  }

}