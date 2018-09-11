/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ait.azure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dwigh
 */
public class QueryCreator {

    private final Connection connection;

    public QueryCreator(Connector connector) {
        this.connection = connector.getConnection();
    }

    public ResultSet getCompanyInformation(String userdomain) {
        ResultSet rs = null;
        try {
            PreparedStatement pst = this.connection.prepareStatement("SELECT * FROM COMPANIES WHERE DOMAIN like ?");
            pst.setString(1, userdomain);
            rs = pst.executeQuery();
        } catch (SQLException e) {
            Logger.getLogger(QueryCreator.class.getName()).log(Level.SEVERE, null, "[QUERY_CREATOR]: error creating statement for company information.");
        }
        return rs;
    }

    public ResultSet getAuthenticationKey(String macAddress, int companyId) {
        ResultSet rs = null;
        try {
            PreparedStatement pst = this.connection.prepareStatement("SELECT AUTHKEY FROM COMPUTERS WHERE MAC like ? AND COMPANYID like ? AND ALLOW='True'");
            pst.setString(1, macAddress);
            pst.setInt(2, companyId);
            rs = pst.executeQuery();
        } catch (SQLException e) {
            Logger.getLogger(QueryCreator.class.getName()).log(Level.SEVERE, null, "[QUERY_CREATOR]: error getting authentication key.");
        }
        return rs;
    }

    public void insertAuthenticationKey(String authKey, String macAddress, int companyId) {
        try {
            PreparedStatement pst = this.connection.prepareStatement("UPDATE computers SET authkey = ? WHERE MAC like ? and companyid like ?");
            pst.setString(1, authKey);
            pst.setString(2, macAddress);
            pst.setInt(3, companyId);
            pst.executeUpdate();
        } catch (SQLException e) {
            Logger.getLogger(QueryCreator.class.getName()).log(Level.SEVERE, null, "[QUERY_CREATOR]: error inserting authentication key.");
        }
    }
    
    public ResultSet getComputerPermissions(String macAddress, String authKey){
        ResultSet rs = null;
        try {
            PreparedStatement pst = this.connection.prepareStatement("SELECT ALLOW FROM COMPUTERS WHERE MAC = ? AND AUTHKEY = ?");
            pst.setString(1, macAddress);
            pst.setString(2, authKey);
            rs = pst.executeQuery();
        } catch(SQLException e){
            Logger.getLogger(QueryCreator.class.getName()).log(Level.SEVERE, null, "[QUERY_CREATOR]: error getting computer permissions.");
        }
        return rs;
    }

    public void closeConnection() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            Logger.getLogger(QueryCreator.class.getName()).log(Level.SEVERE, null, "[QUERY_CREATOR]: error closing connection.");
        }
    }
}
