/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.visumbu.vb.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author dashience
 */
@Entity
@Table(name = "data_source")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DataSource.findAll", query = "SELECT d FROM DataSource d")
    , @NamedQuery(name = "DataSource.findById", query = "SELECT d FROM DataSource d WHERE d.id = :id")
    , @NamedQuery(name = "DataSource.findByAccessToken", query = "SELECT d FROM DataSource d WHERE d.accessToken = :accessToken")
    , @NamedQuery(name = "DataSource.findByCode", query = "SELECT d FROM DataSource d WHERE d.code = :code")
    , @NamedQuery(name = "DataSource.findByConnectionString", query = "SELECT d FROM DataSource d WHERE d.connectionString = :connectionString")
    , @NamedQuery(name = "DataSource.findByDataSourceType", query = "SELECT d FROM DataSource d WHERE d.dataSourceType = :dataSourceType")
    , @NamedQuery(name = "DataSource.findByName", query = "SELECT d FROM DataSource d WHERE d.name = :name")
    , @NamedQuery(name = "DataSource.findByPassword", query = "SELECT d FROM DataSource d WHERE d.password = :password")
    , @NamedQuery(name = "DataSource.findBySqlDriver", query = "SELECT d FROM DataSource d WHERE d.sqlDriver = :sqlDriver")
    , @NamedQuery(name = "DataSource.findByUserName", query = "SELECT d FROM DataSource d WHERE d.userName = :userName")
    , @NamedQuery(name = "DataSource.findByOauthStatus", query = "SELECT d FROM DataSource d WHERE d.oauthStatus = :oauthStatus")})
public class DataSource implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 255)
    @Column(name = "access_token")
    private String accessToken;
    @Size(max = 255)
    @Column(name = "code")
    private String code;
    @Size(max = 255)
    @Column(name = "connection_string")
    private String connectionString;
    @Size(max = 255)
    @Column(name = "data_source_type")
    private String dataSourceType;
    @Size(max = 255)
    @Column(name = "name")
    private String name;
    @Size(max = 255)
    @Column(name = "password")
    private String password;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "source_file")
    private String sourceFile;
    @Size(max = 255)
    @Column(name = "sql_driver")
    private String sqlDriver;
    @Size(max = 255)
    @Column(name = "user_name")
    private String userName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "oauth_status")
    private boolean oauthStatus;

    public DataSource() {
    }

    public DataSource(Integer id) {
        this.id = id;
    }

    public DataSource(Integer id, boolean oauthStatus) {
        this.id = id;
        this.oauthStatus = oauthStatus;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public String getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public String getSqlDriver() {
        return sqlDriver;
    }

    public void setSqlDriver(String sqlDriver) {
        this.sqlDriver = sqlDriver;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean getOauthStatus() {
        return oauthStatus;
    }

    public void setOauthStatus(boolean oauthStatus) {
        this.oauthStatus = oauthStatus;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DataSource)) {
            return false;
        }
        DataSource other = (DataSource) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.visumbu.vb.model.DataSource[ id=" + id + " ]";
    }
    
}
