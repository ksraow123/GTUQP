package in.coempt.util;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

/**
 *
 * @author srinivas
 * @ Globarena Technologies Pvt Ltd.
 */
public class QueryUtil<T> {

    private QueryRunner queryRunner = new QueryRunner();
    private ResultSetHandler<List<T>> listHandler;
    private ResultSetHandler<T> handler;

    public QueryUtil(Class kls) {
        listHandler = new BeanListHandler<T>(kls);
        handler = new BeanHandler<T>(kls);
    }

    public List<T> list(String query, Object... args) {
        Connection conexion = null;
        List<T> list = new ArrayList<T>();
        try {
            conexion = ConnectionUtil.getConnection();
            list = queryRunner.query(conexion, query, listHandler, args);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {                           
                    conexion.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.getLogger(QueryUtil.class.getName()).log(Level.SEVERE, "Failed To Get Database Connection", ex);
            }
        }

        return list;
    }

    public T get(String query, Object id) {
        Connection conexion = null;
        T t = null;
        try {
            conexion = ConnectionUtil.getConnection();
            t = queryRunner.query(conexion, query, handler, id);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                conexion.close();
            } catch (SQLException ex) {
                Logger.getLogger(QueryUtil.class.getName()).log(Level.SEVERE, "Failed To Get Database Connection", ex);
            }
        }

        return t;
    }
    
    public T get(String query, Object... id) {
        Connection conexion = null;
        T t = null;
        try {
            conexion = ConnectionUtil.getConnection();
            t = queryRunner.query(conexion, query, handler, id);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                conexion.close();
            } catch (SQLException ex) {
                Logger.getLogger(QueryUtil.class.getName()).log(Level.SEVERE, "Failed To Get Database Connection", ex);
            }
        }

        return t;
    }

    public int manipulation(String query, Object... id) {
        Connection conexion = null;
        int result = -1;
        try {
            conexion = ConnectionUtil.getConnection();
            result = queryRunner.update(conexion, query, id);
        } catch (SQLException ex) {
            ex.printStackTrace();
            //System.out.println(ex);
        } finally {
            try {
                conexion.close();
            } catch (SQLException ex) {
                Logger.getLogger(QueryUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return result;
    }
    
    public int manipulation(Connection conexion, String query, Object... id) {
//        Connection conexion = null;
        int result = -1;
        try {
//            conexion = ConnectionUtil.getConnection();
            conexion.setAutoCommit(false);
            result = queryRunner.update(conexion, query, id);
        } catch (SQLException ex) {
            ex.printStackTrace();
            //System.out.println(ex);
        } 

        return result;
    }

}
