package Servlets;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;


@WebServlet("/deposito/*")
public class DepositoServlet extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
        JsonObjectBuilder jsonBuilderr = Json.createObjectBuilder();
        JsonObject jsonBuilder = Json.createReader(req.getReader()).readObject();
        String idee1 = jsonBuilder.getString("id");
        Integer id = Integer.parseInt(idee1);
        int quantidade = jsonBuilder.getInt("quantidade");
        if(idee1==null || idee1 ==""){
            jsonBuilderr.add("msg","Erro, ID em falta");
            JsonWriter printjson = Json.createWriter(resp.getWriter());
            printjson.writeObject(jsonBuilderr.build());
            printjson.close();
            }
        else {
            try {
                String query = ("SELECT quantidade FROM stock WHERE id=?");
                PreparedStatement st = null;
                st = c.prepareStatement(query);
                st.setInt(1, id);
                ResultSet results = st.executeQuery();
                if (results.next()) {
                    Integer valor = results.getInt(1);
                    Integer resultado = (valor + quantidade);
                    PreparedStatement sr = c.prepareStatement("UPDATE stock  SET quantidade = ? WHERE id = ?");
                    sr.setInt(1, resultado);
                    sr.setInt(2, id);
                    sr.executeUpdate();
                    c.commit();
                    sr.close();
                    jsonBuilderr.add("msg","Deposito efetuado com sucesso: "+ "id:" + id + " = " + quantidade + " unidades.");
                    JsonWriter printjson = Json.createWriter(resp.getWriter());
                    printjson.writeObject(jsonBuilderr.build());
                    printjson.close();
                } else {
                    jsonBuilderr.add("msg","ID inexistente");
                    JsonWriter printjson = Json.createWriter(resp.getWriter());
                    printjson.writeObject(jsonBuilderr.build());
                    printjson.close();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

}

