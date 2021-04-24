package ch.bzz.refproject.data;

import ch.bzz.refproject.model.Category;
import ch.bzz.refproject.model.Project;
import ch.bzz.refproject.util.Result;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDao implements Dao<Project, String>{
    /**
     * constructor
     */
    public ProjectDao() {}

    private String url = "jdbc:mysql://localhost:3306/refproject";
    private String user = "angehrnj";
    private String pw = "1234";

    @Override
    public List<Project> getAll() {
        List<Project> categoryList = new ArrayList<>();

        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con= DriverManager.getConnection(
                    url,user,pw);
//here sonoo is database name, root is username and password
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT categoryUUID, title, projectUUID, startDate, endDate, status" +
                    " FROM project" +
                    " ORDER BY title asc " +
                    "where status = A");
            while (rs.next()) {
                Project project = new Project();
                categoryList.add(setValues(rs, project));
            }
            con.close();
        }catch(Exception e){ System.out.println(e);}

        return categoryList;
    }

    @Override
    public Project getEntity(String projectUUID) {
        Project project = new Project();
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con= DriverManager.getConnection(
                    url,user,pw);
//here sonoo is database name, root is username and password
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT categoryUUID, title, projectUUID, startDate, endDate, status" +
                    " FROM project" +
                    " WHERE projectUUID = " + projectUUID);
            if (rs.next()) {
                project = setValues(rs, project);
            }
            con.close();
        }catch(Exception e){ System.out.println(e);}

        return project;
    }

    @Override
    public Result save(Project p) {
        int i = 9;

        String v = "";
        v += p.getCategory().getCategoryUUID();
        v += ", ";
        v += p.getTitle();
        v += ", ";
        v += p.getStartDate().toString();
        v += ", ";
        v += p.getEndDate().toString();
        v += ", ";
        v += p.getStatus();

        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con= DriverManager.getConnection(
                    url,user,pw);
//here sonoo is database name, root is username and password
            Statement stmt=con.createStatement();
            i =stmt.executeUpdate("insert into project " +
                    "(projectUUID, categoryUUID, title, startDate, endDate, status) VALUES ("+ p.getProjectUUID() + ", " + v +",) " +
                    "ON DUPLICATE KEY UPDATE" +
                    "(categoryUUID, title, startDate, endDate, status) VALUES (" + v +")");
            con.close();
        }catch(Exception e){ System.out.println(e);}

        switch (i){
            case 0:
                return Result.SUCCESS;

            case 1:
                return Result.NOACTION;

            case 4:
                return Result.DUPLICATE;

            default:
                return Result.ERROR;
        }
    }

    @Override
    public Result delete(String projectUUID) {
        int i = 9;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con= DriverManager.getConnection(
                    url,user,pw);
//here sonoo is database name, root is username and password
            Statement stmt=con.createStatement();
            i = stmt.executeUpdate("DELETE " +
                    " FROM project" +
                    " WHERE projectUUID = " + projectUUID);
            con.close();
        }catch(Exception e){ System.out.println(e);}

        switch (i){
            case 0:
                return Result.SUCCESS;

            case 1:
                return Result.NOACTION;

            case 4:
                return Result.DUPLICATE;

            default:
                return Result.ERROR;
        }
    }

    private Project setValues(ResultSet resultSet, Project project) throws SQLException {
        project.setTitle(resultSet.getString("title"));
        project.setEndDate(resultSet.getDate("endDate").toLocalDate());
        project.setStartDate(resultSet.getDate("startDate").toLocalDate());
        project.setStatus(resultSet.getString("status"));

        Category c = new Category();
        c.setCategoryUUID(resultSet.getString("categoryUUID"));
        project.setCategory(c);

        return project;
    }
}
