package Servlets;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;


@WebServlet("/items/*")
public class ItemsServlet extends HttpServlet {
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
        }catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        JsonObjectBuilder jsonBuilderr = Json.createObjectBuilder();
        Statement st = null;
            try {
                st = c.createStatement();
                String query = ("SELECT * FROM stock");
                ResultSet results = st.executeQuery(query);
                JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
                JsonArrayBuilder Array = Json.createArrayBuilder();
                while (results.next()){
                    JsonObjectBuilder builderItems = Json.createObjectBuilder();
                    String nome = results.getString(2);
                    Integer ide= results.getInt(1);
                    Integer quantidade= results.getInt(3);
                    String descricao= results.getString(4);
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
                jsonBuilderr.add("msg","Não existem items");
            }
        }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
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

        JsonObject jsonBuilder = Json.createReader(req.getReader()).readObject();
        JsonObjectBuilder jsonBuilderr = Json.createObjectBuilder();

        String nome1 = jsonBuilder.getString("nome");
        if(nome1==null|| nome1==""){
            jsonBuilderr.add("msg","Nome inexistente");
        }else {
            PreparedStatement ad = null;
            try {
                String query = ("SELECT * FROM stock WHERE nome=?");
                PreparedStatement st = null;
                st = c.prepareStatement(query);
                st.setString(1, nome1);
                ResultSet results = st.executeQuery();
                if(results.next()){
                    jsonBuilderr.add("msg","Erro, item já existe");
                }else {
                    ad = c.prepareStatement("INSERT INTO stock (nome) VALUES (?)");
                    ad.setString(1, nome1);
                    ad.executeUpdate();
                    c.commit();
                    ad.close();
                    jsonBuilderr.add("msg","Item adicionado com sucesso: " +nome1);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }



        }
    }

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
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        JsonObjectBuilder jsonBuilderr = Json.createObjectBuilder();
        JsonObject jsonBuilder = Json.createReader(req.getReader()).readObject();
        String idee1 = jsonBuilder.getString("id");
        if (idee1 == null || idee1 == "") {
            jsonBuilderr.add("msg","Erro, ID em falta");

        } else {
            try {
                String queryy = ("SELECT * FROM stock WHERE id=?");
                Integer id = Integer.parseInt(idee1);
                String descricao1 = jsonBuilder.getString("descricao");
                PreparedStatement ste = c.prepareStatement(queryy);
                ste.setInt(1, id);
                ResultSet results = ste.executeQuery();
                if (results.next()) {
                    PreparedStatement sta = c.prepareStatement("UPDATE stock  SET descricao = ? WHERE id = ?");
                    sta.setString(1, descricao1);
                    sta.setInt(2, id);
                    sta.executeUpdate();
                    c.commit();
                    sta.close();
                    jsonBuilderr.add("msg","Descricao alterada com sucesso para o ID=" + id);

                } else {
                    jsonBuilderr.add("msg","ID inexistente");

                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
        String nome = jsonBuilder.getString("nome");

        if(nome == null || nome==""){
            jsonBuilderr.add("msg","Erro, é preciso inserir um nome");
        }else{
            try {
                String query = ("SELECT * FROM stock WHERE nome=?");
                PreparedStatement st = c.prepareStatement(query);
                st.setString(1, nome);
                ResultSet results = st.executeQuery();
                if (results.next()){
                    Integer quantidade = results.getInt(3);
                        if(quantidade == 0){
                            String querya = ("SELECT * FROM entregas WHERE nomeproduto=?");
                            PreparedStatement ste = c.prepareStatement(querya);
                            ste.setString(1, nome);
                            ResultSet resultse = ste.executeQuery();
                            if(resultse.next()){
                                jsonBuilderr.add("msg","Lamentamos, mas existem entregas com esse produto.");

                            }else{
                                PreparedStatement sti = c.prepareStatement("DELETE FROM stock WHERE nome = ?");
                                sti.setString(1, nome);
                                sti.executeUpdate();
                                c.commit();
                                sti.close();
                                jsonBuilderr.add("msg","Item removido com sucesso: "+nome);
                            }
                    }else{
                            jsonBuilderr.add("msg","Lamentamos, mas este item está em stock, não é possivel removê-lo.");
                    }
                }else{
                    jsonBuilderr.add("msg","Nome Inexistente");
                }
                st.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
