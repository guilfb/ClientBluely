<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<style>
    tr:nth-child(even) {
        background-color: #f2f2f2;
    }

    body {
        font-family: "Lato", sans-serif;
        transition: background-color .5s;
        transition: margin-left .5s;
        padding: 16px;
    }

    /* The side navigation menu */
    .sidenav {
        height: 100%; /* 100% Full-height */
        width: 0; /* 0 width - change this with JavaScript */
        position: fixed; /* Stay in place */
        z-index: 1; /* Stay on top */
        top: 0; /* Stay at the top */
        left: 0;
        background-color: #111; /* Black*/
        overflow-x: hidden; /* Disable horizontal scroll */
        padding-top: 60px; /* Place content 60px from the top */
        transition: 0.5s; /* 0.5 second transition effect to slide in the sidenav */
    }

    /* The navigation menu links */
    .sidenav a {
        padding: 8px 8px 8px 32px;
        text-decoration: none;
        font-size: 15px;
        color: #818181;
        display: block;
        transition: 0.3s;
    }

    /* When you mouse over the navigation links, change their color */
    .sidenav a:hover {
        color: #f1f1f1;
    }

    /* Position and style the close button (top right corner) */
    .sidenav .closebtn {
        position: absolute;
        top: 0;
        right: 25px;
        font-size: 36px;
        margin-left: 50px;
    }

    /* On smaller screens, where height is less than 450px, change the style of the sidenav (less padding and a smaller font size) */
    @media screen and (max-height: 450px) {
        .sidenav {padding-top: 15px;}
        .sidenav a {font-size: 18px;}
    }
</style>

<header>
    <div id="mySidenav" class="sidenav">
        <a href="javascript:void(0)" class="closebtn" onclick="closeNav()">&times;</a>
        <c:if test="${sessionScope.id == null }">
            <a href="Controleur?action=formLogin">
                <span class="glyphicon glyphicon-user"></span>
                Se Connecter
                <span class="caret"></span>
            </a>
            <a href="Controleur?action=creerCompte">
                <span class="glyphicon glyphicon-plus-sign"></span>
                Créer un compte
                <span class="caret"></span>
            </a>
        </c:if>
        <c:if test="${sessionScope.id > 0  }">
            <li>
                <c:out value="${sessionScope.nomUtilisateur}" />
            </li>
            <li><a href="Controleur?action=map">MAP</a></li>
            <li>
                <a href="Controleur?action=monEspace">
                    <span class="glyphicon glyphicon-user"></span>
                    Mon espace
                </a>
            </li>
            <li><a href="Controleur?action=logout" style="fontSize: 15px;"><span class="glyphicon glyphicon-log-out"></span> Quitter</a></li>
        </c:if>
    </div>

    <div class="row" style="margin-top: 10px;">
        <div class="col-sm-1"><span style="font-size:35px;cursor:pointer;margin-right: 1%;" onclick="openNav();">&#9776;</span></div>
        <div class="col-sm-2"></div>
        <div class="col-sm-6 titreAChanger" style="border-top:1px solid grey;display: flex; flex-direction: column; justify-content:center; text-align:center; font-size: 40px;"></div>
        <div class="col-sm-3"></div>
    </div>
</header>

<script>
    /* Set the width of the side navigation to 250px and the left margin of the page content to 250px and add a black background color to body */
    function openNav() {
        document.getElementById("mySidenav").style.width = "250px";
        document.body.style.marginLeft = "250px";
    }

    /* Set the width of the side navigation to 0 and the left margin of the page content to 0, and the background color of body to white */
    function closeNav() {
        document.getElementById("mySidenav").style.width = "0";
        document.body.style.marginLeft = "0";
    }
</script>