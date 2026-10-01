package edu.nuos.lab2;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "Task1Servlet", value = "/task1")
public class Task1Servlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            double x = Double.parseDouble(req.getParameter("x").trim().replace(',', '.'));
            int n = Integer.parseInt(req.getParameter("n").trim());
            double e1 = Double.parseDouble(req.getParameter("e1").trim().replace(',', '.'));
            double e2 = Double.parseDouble(req.getParameter("e2").trim().replace(',', '.'));

            // a. Сума n доданків: a_k = a_{k-1} * (-x) / k, a_0 = 1
            double sumN = 0.0;
            double term = 1.0;
            for (int k = 0; k < n; k++) {
                sumN += term;
                term = term * (-x) / (k + 1);
            }

            // b. Доданки, які за модулем > e1
            double sumE1 = 0.0;
            int countE1 = 0;
            term = 1.0;
            int k = 0;
            while (Math.abs(term) > e1 && k < 10000) {
                sumE1 += term;
                countE1++;
                k++;
                term = term * (-x) / k;
            }

            // b. Доданки, які за модулем > e2
            double sumE2 = 0.0;
            int countE2 = 0;
            term = 1.0;
            k = 0;
            while (Math.abs(term) > e2 && k < 10000) {
                sumE2 += term;
                countE2++;
                k++;
                term = term * (-x) / k;
            }

            // c. Точне значення функції
            double exact = Math.exp(-x);

            out.println("<!DOCTYPE html><html lang='uk'><head><meta charset='UTF-8'>");
            out.println("<title>Результати 2.1</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; background: #edf2f7; margin: 40px; }");
            out.println(".container { max-width: 700px; margin: auto; background: white; padding: 25px; border-radius: 8px; }");
            out.println("table { width: 100%; border-collapse: collapse; margin-top: 15px; }");
            out.println("th, td { border: 1px solid #cbd5e0; padding: 10px; text-align: left; }");
            out.println("th { background: #3182ce; color: white; }");
            out.println("a.btn { display: inline-block; margin-top: 15px; padding: 8px 14px; background: #4a5568; color: white; text-decoration: none; border-radius: 4px; }");
            out.println("</style></head><body><div class='container'>");

            out.println("<h2>Результати обчислень для функції e<sup>-x</sup></h2>");
            out.println("<table>");
            out.println("<tr><th>Параметр</th><th>Результат</th></tr>");
            out.println("<tr><td>Вхідний x</td><td>" + x + "</td></tr>");
            out.println("<tr><td>Точне значення e<sup>-x</sup></td><td><strong>" + exact + "</strong></td></tr>");
            out.println("<tr><td>Сума " + n + " доданків</td><td>" + sumN + " (похибка: " + Math.abs(exact - sumN) + ")</td></tr>");
            out.println("<tr><td>Сума доданків (|a<sub>k</sub>| &gt; " + e1 + ")</td><td>" + sumE1 + " (кількість: " + countE1 + ")</td></tr>");
            out.println("<tr><td>Сума доданків (|a<sub>k</sub>| &gt; " + e2 + ")</td><td>" + sumE2 + " (кількість: " + countE2 + ")</td></tr>");
            out.println("</table>");

            out.println("<br><a href='task1.html' class='btn'>&larr; Назад</a> <a href='index.html' class='btn'>На головну</a>");
            out.println("</div></body></html>");

        } catch (Exception e) {
            out.println("<p style='color:red;'>Помилка обробки даних: " + e.getMessage() + "</p>");
            out.println("<a href='task1.html'>Спробувати знову</a>");
        }
    }
}