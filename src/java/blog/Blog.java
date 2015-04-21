/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blog;

import database.Connect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;

import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 *
 * @author chintan
 */
@Path("/blog")
public class Blog {

    @GET
    @Produces("application/json")

    public Response getAll() {
        return Response.ok(getResult("select * from blog")).build();

    }

    private static JsonArray getResult(String query, String... parameters) {
        JsonArray json = null;
        try {
            Connection con = Connect.getConnection();
            PreparedStatement pst = con.prepareStatement(query);
            for (int i = 0; i < parameters.length; i++) {
                pst.setString(i + 1, parameters[i]);
            }
            ResultSet results = pst.executeQuery();

            JsonArrayBuilder array = Json.createArrayBuilder();
            while (results.next()) {
                array.add(Json.createObjectBuilder()
                        .add("blog_id", results.getString("blog_id"))
                        .add("user_id", results.getString("user_id"))
                        .add("blog_date_time", results.getString("blog_date_time"))
                        .add("title", results.getString("title"))
                        .add("content", results.getString("content"))
                );
            }
            json = array.build();
        } catch (SQLException ex) {
            Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
        }
        return json;
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response add(JsonObject json) {

        String id = json.getString("user_id");
        String title = json.getString("title");
        String blog_time = json.getString("blog_date_time");
        String content = json.getString("content");
      
        
        int result = doUpdate("INSERT INTO blog (user_id,blog_date_time,title,content) VALUES ( ?, ?, ?,?)", id, blog_time, title, content);

        if (result <= 0) {
            return Response.status(500).build();

        } else {
            return Response.ok(json).build();
        }
    }

      
    @PUT
    @Path("{id}")
    public String put(@PathParam("id") int id,JsonObject json) {
        try {
           
           String user_id = json.getString("user_id");
        String title = json.getString("title");
        String blog_time = json.getString("blog_date_time");
        String content = json.getString("content");
              
            
            Connection conn = Connect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE blog SET user_id=?, title=?,blog_date_time=?, content=? WHERE blog_id=?");
            pstmt.setString(1, user_id);
            pstmt.setString(2, title);
            pstmt.setString(3, blog_time);
            pstmt.setString(4, content);
            pstmt.setInt(5, id);
            
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Blog.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "updated";
    }

    private int doUpdate(String query, String... params) {
        int numChanges = 0;
        try (Connection con = Connect.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            numChanges = pstmt.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(Blog.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return numChanges;
    }
    
        
    @GET
    @Path("{id}")
    public String get(@PathParam("id") int id) {
        String result = "";
        try {
            Connection conn = Connect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM blog WHERE blog_id = ?");
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            JsonObject json = Json.createObjectBuilder()
                    .add("title", rs.getString("title"))
                    .add("text", rs.getString("text"))
                    .add("time", rs.getString("time"))
                    .add("id", rs.getInt("id"))
                    .build();                           
            result = json.toString();
        } catch (SQLException ex) {
            Logger.getLogger(Blog.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

}
