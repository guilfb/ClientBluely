<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@include file="header.jsp" %>
<body>
<%@include file="navigation.jsp"%>
<H1> Création d'un compte </H1>
<form method="post" action="Controleur?action=insererClient">
    <div class="col-md-12 well well-md">
        <div class="row" >
            <div class="col-md-12" style ="border:none; background-color:transparent; height :20px;">
            </div>
        </div>
        <div class="form-group">
            <label class="col-md-3 control-label">Nom : </label>
            <div class="col-md-3">
                <INPUT type="text" name="nom" value='' id="nom" class="form-control" min="0">
            </div>
        </div>
        <div class="form-group">
            <label class="col-md-3 control-label">Prenom : </label>
            <div class="col-md-3">
                <INPUT type="text" name="prenom" value='' id="prenom" class="form-control" min="0">
            </div>
        </div>
        <div class="row" >
            <div class="col-md-12" style ="border:none; background-color:transparent; height :20px;">
            </div>
        </div>
        <div class="form-group">
            <label class="col-md-3 control-label">Date de naissance : </label>
            <div class="col-md-3">
                <input type="date" name="datenaissance" min="1900-12-31" max="2018-12-31"><br>
            </div>
        </div>
        <div class="row" >
            <div class="col-md-12" style ="border:none; background-color:transparent; height :20px;">
            </div>
        </div>
        <div class="form-group">
            <label class="col-md-3 control-label">Login : <c:out value="${MesErreurs}"> </c:out></label>
            <div class="col-md-3">
                <INPUT type="text" name="login" value='' id="login" class="form-control" min="0">
            </div>
            <label class="col-md-3 control-label">Mot de passe : </label>
            <div class="col-md-3">
                <input type="password" name="password">
            </div>

        </div>
        <div class="row" >
            <div class="col-md-12" style ="border:none; background-color:transparent; height :20px;">
            </div>
        </div>

        <div class="form-group">
            <button type="submit" class="btn btn-default btn-primary"><span class="glyphicon glyphicon-ok"></span>
                Valider
            </button>

            <button type="button" class="btn btn-default btn-primary"
                    onclick="{
                            window.location = 'index.jsp';
                        }">
                <span class="glyphicon glyphicon-remove"></span> Annuler

            </button>
        </div>
    </div>
</form>
</body>
<%@include file="footer.jsp"%>
</html>