package Servlets;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet("/entregas/*")
public class EntregasServlet extends HttpServlet {
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
                String query = ("SELECT * FROM entregas");
                ResultSet results = st.executeQuery(query);
                JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
                JsonArrayBuilder Array = Json.createArrayBuilder();
                while (results.next()){
                    JsonObjectBuilder builderItems = Json.createObjectBuilder();
                    String nome = results.getString(2);
                    Integer ide= results.getInt(1);
                    Integer quantidade= results.getInt(3);
                    String local= results.getString(4);
                    builderItems.add("Id", ide);
                    builderItems.add("Nome", nome);
                    builderItems.add("Quantidade", quantidade);
                    builderItems.add("Local", local);
                    Array.add(builderItems);
                }
                jsonBuilder.add("Entregas",Array);
                JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
                jsonWriter.writeObject(jsonBuilder.build());
                st.close();
                jsonWriter.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
        JsonObject jsonBuilder = Json.createReader(req.getReader()).readObject();

        String id1 = jsonBuilder.getString("id");
        int numeroitems1 = jsonBuilder.getInt("numeroitems");
        String local = jsonBuilder.getString("local");
        Integer id = Integer.parseInt(id1);

        JsonObjectBuilder jsonBuilderr = Json.createObjectBuilder();

        if(id1 == null || id1=="" || local==null || local=="" ){
            jsonBuilderr.add("msg","Erro, parametros em falta");
            JsonWriter printjson = Json.createWriter(resp.getWriter());
            printjson.writeObject(jsonBuilderr.build());
            printjson.close();
        }else {
            if(numeroitems1 >0){
                PreparedStatement stg = null;
                String queryy= ("SELECT * FROM entregas WHERE id= ?");
                try {
                    stg = c.prepareStatement(queryy);
                    stg.setInt(1,id);
                    ResultSet resultsi = stg.executeQuery();
                    if(resultsi.next()){
                        jsonBuilderr.add("msg","ID de entrega ja existente, tente outro");
                        JsonWriter printjson = Json.createWriter(resp.getWriter());
                        printjson.writeObject(jsonBuilderr.build());
                        printjson.close();
                    }else{
                        for (int i = 1; i <= numeroitems1; i++) {
                            String nomeproduto = jsonBuilder.getString("nomeitem"+i);
                            int quantidade = jsonBuilder.getInt("quantidade"+i);
                            PreparedStatement ad = null;
                            String query = ("SELECT quantidade FROM stock WHERE nome=?");
                            PreparedStatement st = null;
                            try {
                                st = c.prepareStatement(query);
                                st.setString(1, nomeproduto);
                                ResultSet results = st.executeQuery();
                                if (results.next()){
                                    Integer valor = results.getInt(1);
                                    Integer resultado = (valor - quantidade);
                                    if(resultado >= 0){
                                        PreparedStatement as = c.prepareStatement("UPDATE stock  SET quantidade = ? WHERE nome = ?"); // subtrai a quantia para ser entregue no armazem
                                        as.setInt(1, resultado);
                                        as.setString(2,nomeproduto);
                                        as.executeUpdate();
                                        c.commit();
                                        as.close();

                                        ad = c.prepareStatement("INSERT INTO entregas (id,nomeproduto,quantidade,locall) VALUES (?,?,?,?)");
                                        ad.setInt(1, id);
                                        ad.setString(2, nomeproduto);
                                        ad.setInt(3, quantidade);
                                        ad.setString(4, local);
                                        ad.executeUpdate();
                                        c.commit();
                                        ad.close();

                                        jsonBuilderr.add("msg","Entrega id: "+id+"; Nome Produto: "+nomeproduto+"; Local: "+ local);
                                        JsonWriter printjson = Json.createWriter(resp.getWriter());
                                        printjson.writeObject(jsonBuilderr.build());
                                    }else{
                                        jsonBuilderr.add("msg","Quantia em armazem nao suficiente para a entrega do item:" +nomeproduto);
                                        JsonWriter printjson = Json.createWriter(resp.getWriter());
                                        printjson.writeObject(jsonBuilderr.build());
                                        printjson.close();
                                    }
                                }else{
                                    jsonBuilderr.add("msg","Item inexistente no armazem");
                                    JsonWriter printjson = Json.createWriter(resp.getWriter());
                                    printjson.writeObject(jsonBuilderr.build());
                                    printjson.close();
                                }
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                        }
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }


        }else{
                jsonBuilderr.add("msg","Precisas de inserir uma quantiadde de items que deseja entregar");
                JsonWriter printjson = Json.createWriter(resp.getWriter());
                printjson.writeObject(jsonBuilderr.build());
                printjson.close();
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        Connection c = null;
        JsonObjectBuilder jsonBuilderr = Json.createObjectBuilder();
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

        String id = jsonBuilder.getString("id");
        String local = jsonBuilder.getString("local");

        Integer id1 = Integer.parseInt(id);
        PreparedStatement st = null;
        try {
            String query = ("SELECT 1 FROM entregas WHERE id=?");
            st = c.prepareStatement(query);
            st.setInt(1, id1);
            ResultSet results = st.executeQuery();
            if(results.next()){
                PreparedStatement ste = c.prepareStatement("UPDATE entregas SET locall = ? WHERE id = ?");
                ste.setString(1, local);
                ste.setInt(2, id1);
                ste.executeUpdate();
                c.commit();
                ste.close();
                jsonBuilderr.add("msg","Local alterado com sucesso");
                JsonWriter printjson = Json.createWriter(resp.getWriter());
                printjson.writeObject(jsonBuilderr.build());
                printjson.close();
            }else{
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