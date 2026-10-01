package edu.nuos.lab2;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@WebServlet(name = "Task2Servlet", value = "/task2")
public class Task2Servlet extends HttpServlet {

    static class DataPoint {
        double x;
        Double y;
        DataPoint(double x, Double y) { this.x = x; this.y = y; }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            double a = Double.parseDouble(req.getParameter("a").trim().replace(',', '.'));
            double b = Double.parseDouble(req.getParameter("b").trim().replace(',', '.'));
            double h = Double.parseDouble(req.getParameter("h").trim().replace(',', '.'));

            if (a >= b || h <= 0) {
                throw new IllegalArgumentException("Некоректні межі або крок!");
            }

            List<DataPoint> points = new ArrayList<>();
            for (double cur = a; cur <= b + 1e-9; cur += h) {
                double cosX = Math.cos(cur);
                Double yVal = null;
                // tg(x) не визначений при cos(x)=0, а знаменник 1-tg(x) != 0
                if (Math.abs(cosX) > 1e-7) {
                    double tanX = Math.tan(cur);
                    if (Math.abs(1.0 - tanX) > 1e-7) {
                        yVal = cur / (1.0 - tanX);
                    }
                }
                points.add(new DataPoint(cur, yVal));
            }

            out.println("<!DOCTYPE html><html lang='uk'><head><meta charset='UTF-8'>");
            out.println("<title>Табулювання та Графік</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; background: #f7fafc; margin: 30px; }");
            out.println(".container { max-width: 950px; margin: auto; background: white; padding: 25px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }");
            out.println(".grid { display: flex; gap: 20px; margin-top: 20px; }");
            out.println(".table-wrap { flex: 1; max-height: 420px; overflow-y: auto; border: 1px solid #e2e8f0; }");
            out.println(".chart-wrap { flex: 1.4; text-align: center; }");
            out.println("table { width: 100%; border-collapse: collapse; }");
            out.println("th, td { border: 1px solid #e2e8f0; padding: 6px 10px; text-align: center; }");
            out.println("th { background: #38a169; color: white; position: sticky; top: 0; }");
            out.println("canvas { border: 1px solid #cbd5e0; background: #fff; }");
            out.println("a.btn { display: inline-block; margin-top: 15px; padding: 8px 14px; background: #4a5568; color: white; text-decoration: none; border-radius: 4px; }");
            out.println("</style></head><body><div class='container'>");

            out.println("<h2>Табулювання функції y = x / (1 - tg(x))</h2>");
            out.println("<div class='grid'>");

            // Таблица
            out.println("<div class='table-wrap'><table><tr><th>x</th><th>y</th></tr>");
            for (DataPoint p : points) {
                out.printf(Locale.US, "<tr><td>%.4f</td><td>%s</td></tr>",
                        p.x, (p.y != null ? String.format(Locale.US, "%.5f", p.y) : "Не визначено"));
            }
            out.println("</table></div>");

            // График (Canvas)
            out.println("<div class='chart-wrap'><canvas id='plot' width='480' height='400'></canvas></div>");
            out.println("</div>");

            // JS для рендеринга осей и графика
            out.println("<script>");
            out.println("const pts = [");
            for (DataPoint p : points) {
                out.printf(Locale.US, "{x: %.4f, y: %s},", p.x, (p.y != null ? String.format(Locale.US, "%.5f", p.y) : "null"));
            }
            out.println("];");
            out.println("const cvs = document.getElementById('plot'); const ctx = cvs.getContext('2d');");
            out.println("const valid = pts.filter(p => p.y !== null);");
            out.println("if (valid.length > 1) {");
            out.println("  const xs = valid.map(p => p.x); const ys = valid.map(p => p.y);");
            out.println("  const minX = Math.min(...xs), maxX = Math.max(...xs);");
            out.println("  const minY = Math.min(...ys), maxY = Math.max(...ys);");
            out.println("  const p = 40, w = cvs.width - 2*p, h = cvs.height - 2*p;");
            out.println("  const mapX = x => p + ((x - minX) / (maxX - minX || 1)) * w;");
            out.println("  const mapY = y => cvs.height - p - ((y - minY) / (maxY - minY || 1)) * h;");
            out.println("  ctx.strokeStyle = '#a0aec0'; ctx.strokeRect(p, p, w, h);");
            out.println("  ctx.fillStyle = '#2d3748'; ctx.font = '12px sans-serif';");
            out.println("  ctx.fillText('X: [' + minX.toFixed(2) + ' ; ' + maxX.toFixed(2) + ']', p, cvs.height - 10);");
            out.println("  ctx.fillText('Y: [' + minY.toFixed(2) + ' ; ' + maxY.toFixed(2) + ']', 5, p - 10);");
            out.println("  ctx.strokeStyle = '#38a169'; ctx.lineWidth = 2; ctx.beginPath();");
            out.println("  let drawing = false;");
            out.println("  pts.forEach(pt => {");
            out.println("    if (pt.y !== null) {");
            out.println("      const cx = mapX(pt.x), cy = mapY(pt.y);");
            out.println("      if (!drawing) { ctx.moveTo(cx, cy); drawing = true; } else { ctx.lineTo(cx, cy); }");
            out.println("    } else { drawing = false; }");
            out.println("  });");
            out.println("  ctx.stroke();");
            out.println("}");
            out.println("</script>");

            out.println("<br><a href='task2.html' class='btn'>&larr; Назад</a> <a href='index.html' class='btn'>На головну</a>");
            out.println("</div></body></html>");

        } catch (Exception e) {
            out.println("<p style='color:red;'>Помилка: " + e.getMessage() + "</p>");
            out.println("<a href='task2.html'>Спробувати знову</a>");
        }
    }
}