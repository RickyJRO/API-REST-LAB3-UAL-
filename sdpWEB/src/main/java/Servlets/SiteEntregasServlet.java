package Servlets;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@WebServlet("/ClientEntregas")
public class SiteEntregasServlet extends HttpServlet {
    static String PAGE_HEADER = "<html><head>" +
            "<link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta1/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-giJF6kkoqNQ00vy+HMDP7azOuL0xtbfIcaT9wjKHr8RbDVddVHyTfAAsrekwKmP1\" crossorigin=\"anonymous\">" +
            "<title>PROJETO SDP</title></head><body>";

    static String PAGE_FOOTER = "<script src=\"https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.4/dist/umd/popper.min.js\" integrity=\"sha384-q2kxQ16AaE6UbzuKqyBE9/u/KzioAlnx2maXQHiDX9d4/zp8Ok3f+M7DPm+Ib6IU\" crossorigin=\"anonymous\"></script>\n" +
            "<script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta1/dist/js/bootstrap.min.js\" integrity=\"sha384-pQQkAEnwaBkjpqZ8RU1fF1AKtTcHJwFl3pblpTlHXybJjHpMYo79HY3hIi4NKxyj\" crossorigin=\"anonymous\"></script>" +
            "</body></html>";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();
        writer.println(PAGE_HEADER);
        writer.println("" +
                "<style>" +
                "body{" +
                "background-image: url('https://images4.alphacoders.com/556/556073.jpg');" +
                "background-size: cover;" +
                "background-repeat: no-repeat;" +
                "}" +
                        "a {" +
                        "  color: white;" +
                        "  text-decoration: none;" +
                        "}"+
                "</style>" +
                "<script>" +
                "function mostrarEntregas(){" +
                "document.getElementById('tabelaEntregas').style.display='block'" +
                "}" +
                "</script>" +
                "<div class='text-center'>" +
                "<h1 class='display-1 text-light font-weight-bold mt-5'>Consulta de Entregas</h1>" +
                "<button class='btn btn-danger mt-5' onclick='mostrarEntregas()'>Mostrar Entregas</button>" +
                "</div>" +
                "" +
                "" +
                "" +
                "" +
                "<div id='tabelaEntregas' style='display:none;'>" +
                "<div class='container'>"+
                "<table style='width:100%' class='table table-dark mt-5 table-bordered'>" +
                "<thead class='thead-danger'>" +
                "<tr>" +
                "<th scope='col'>Id</th>" +
                "<th scope='col'>Nome</th>" +
                "<th scope='col'>Quantidade</th>" +
                "<th scope='col'>Local</th>" +
                "</tr>" +
                "</thead>");

        try {
            HttpURLConnection con = (HttpURLConnection) new URL("http://containerrest:8080/entregas").openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String stringJson = reader.readLine();
            JSONObject object = new JSONObject(stringJson);
            JSONArray items = object.getJSONArray("Entregas");

            for(int i = 0; i < items.length(); i++){
                JSONObject itemObject = items.getJSONObject(i);
                int idItem = itemObject.getInt("Id");
                String name = itemObject.getString("Nome");
                int quantity = itemObject.getInt("Quantidade");
                String local = itemObject.getString("Local");

                writer.println(
                        "<tbody>" +
                                "<tr>" +
                                "<th>"+ idItem +"</th>" +
                                "<th>"+ name +"</th>" +
                                "<th>"+ quantity +"</th>" +
                                "<th>"+ local+"</th>" +
                                "</tr>" +
                                "</tbody>"+
                                "</div>" +
                                "</div>" +
                                "</div>");

            }

            writer.println("</table>" +
                    "<div class='class='col-xs-3'>" +
                    "<button class='btn btn-danger mt-5 ml-3'><a href='http://localhost:8081/ClientItemsStock'>Ir para Items em Stock</a></button>" +
                    "</div>"+
            "<button class='btn btn-danger mt-5'><a href='http://localhost:8081/ClientItems'>Ir para Items</a></button>");
            writer.println(PAGE_FOOTER);
            writer.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }
}