<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>

<%@include file="header.jsp" %>
<body>
<%@include file="navigation.jsp"%>

	<div class="jumbotron text-center">
		<h1>Rendre votre <c:out value="${vehicule.typeVehicule.typeVehicule}"> </c:out> à la station <c:out value="${station.adresse}"> </c:out> </h1>
	</div>

	<div class="container">
		<a class="btn btn-secondary" href="index.jsp" role="button"><span class="glyphicon glyphicon-menu-left"></span> Retour accueil</a>
		<h2>Bornes disponibles</h2>
		<div class="container">
			<table class="table table-hover">
				<tr>
					<th class="col-md-2">Numéro de la borne</th>
					<th class="col-md-2"> </th>
				</tr>
				<c:forEach items="${bornes}" var="item">
					<tr>
						<td>${item.idBorne}</td>
						<td><a class="btn btn-info" href="Controleur?action=rendre&idBorne=${item.idBorne}&idVehicule=${vehicule.idVehicule}" role="button"><span
								class="glyphicon glyphicon-arrow-down"></span> Rendre à cette borne</a></td>
					</tr>
				</c:forEach>
			</table>
		</div>
    </div>
<%@include file="footer.jsp"%>
</body>
</html>