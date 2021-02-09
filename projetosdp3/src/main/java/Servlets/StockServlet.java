package Servlets;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;


@WebServlet("/stock/*")
public class StockServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://basedados:5432/basedados",
                            "postgres", "foobar");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");
        }catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        Statement st = null;
            try {
                st = c.createStatement();
                String query = ("SELECT * FROM stock WHERE quantidade>0");
                ResultSet resultse = st.executeQuery(query);
                JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
                JsonArrayBuilder Array = Json.createArrayBuilder();
                while (resultse.next()){
                    JsonObjectBuilder builderItems = Json.createObjectBuilder();
                    String nome = resultse.getString(2);
                    Integer ide= resultse.getInt(1);
                    Integer quantidade= resultse.getInt(3);
                    String descricao= resultse.getString(4);
                    builderItems.add("Id", ide);
                    builderItems.add("Nome", nome);
                    builderItems.add("Quantidade", quantidade);
                    builderItems.add("Descricao", descricao);
                    Array.add(builderItems);
                }
                jsonBuilder.add("Items",Array);
                JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
                jsonWriter.writeObject(jsonBuilder.build());
                st.close();
                jsonWriter.close();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
}