<!DOCTYPE html>
<html>
<head>
    <title>KPrime</title>
    <link rel="icon" href="/img/favicon.png">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <script src="js/vue.js"></script>
    <script src="js/vue-resources.js"></script>
    <script defer src="js/fontawesome.js"></script>

    <script src="js/jquery.js" ></script>
    <script src="js/jpopper.js" ></script>
    <link rel="stylesheet" href="/css/bootstrap.css" >
    <script src="js/bootstrap.js"></script>

    <link href="https://fonts.googleapis.com/css?family=Alegreya+Sans+SC|Jura:500&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="/css/kprime.css">
</head>
<body style="background-image:url('/img/arabesque7x.jpeg')">
<nav aria-label="breadcrumb">
    <ol class="breadcrumb">
        <li class="breadcrumb-item"><a href="http://kprime.it/"><img height="50px" src="/img/kprime-logo-bw.png" alt="KPrime"></a></li>
        <li class="breadcrumb-item active "><img height="40px" src="/img/login.png" alt="Login"> Login</li>
        <li class="breadcrumb-item "><a href="/search.html"><img height="50px" src="/img/search.png" alt="Search"> Search </a></li>
        <li class="breadcrumb-item "><a href="/project/"><img height="50px" src="/img/project_b.png" alt="Projects"> Contexts </a></li>
    </ol>
</nav>

<main>
    <div id="content">

<div class="container">
    <div class="kprime-panel">
    <img src="/img/kprime-logo-bw.png"></img>
    <hr/>
    <div class="row">
        <div class="col">
            <div class="card">
                <div class="card-body">
                    <form id="loginForm" method="post">
                        <#if authenticationFailed>
                        <div class="alert alert-danger" role="alert">
                            <p class="bad notification">${msg.get("LOGIN_AUTH_FAILED")}</p>
                        </div>
                        <#elseif authenticationSucceeded>
                        <div class="alert alert-success" role="alert">
                            <p class="good notification">${msg.get("LOGIN_AUTH_SUCCEEDED", $currentUser)}</p>
                        </div>
                        <#elseif loggedOut>
                        <div class="alert alert-success" role="alert">
                            <p class="notification">${msg.get("LOGIN_LOGGED_OUT")}</p>
                        </div>
                        </#if>
                        <h1>${msg.get("LOGIN_HEADING")}</h1>
                        <hr/>
<!--                        <label>${msg.get("LOGIN_LABEL_USERNAME")}</label>-->
                        <input type="text" name="username" placeholder="${msg.get("LOGIN_LABEL_USERNAME")}" value="" required autofocus >
<!--                        <label>${msg.get("LOGIN_LABEL_PASSWORD")}</label>-->
                        <input type="password" name="password" placeholder="${msg.get("LOGIN_LABEL_PASSWORD")}" value="" required >
                        <#if loginRedirect != "">
                        <input type="hidden" name="loginRedirect" value="$loginRedirect">
                        </#if>
                        <hr/>
                        <input class="btn btn-success" type="submit" value="${msg.get("LOGIN_BUTTON_LOGIN")}">
                        <hr/>
                    <#if hasGitHubKey>
                        <a href="/github"><img height="50px" src="/img/github.png" /> GitHub Login</a>
                    </#if>
                    </form>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card">
                <div class="card-body">
                    <h1>${msg.get("LOGIN_PROJECTS")}</h1>
                    <label>${msg.get("LOGIN_LABEL_PROJECTS")}</label><br/>
                    <a class="btn btn-success" href="/project/">${msg.get("LOGIN_BUTTON_LIST")}</a>
                </div>
            </div>
            <div class="card">
                <div class="card-body">
                    <h1>${msg.get("LOGIN_SEARCH")}</h1>
                    <label>${msg.get("LOGIN_LABEL_SEARCH")}</label><br/>
                    <a class="btn btn-success" href="/search.html">${msg.get("LOGIN_BUTTON_SEARCH")}</a>
                </div>
            </div>
        </div>
        </div>
    </div>



</div>
</main>
<footer class="pt-4 my-md-5 pt-md-12 border-top kprime-panel">
<!-- ${msg.get("COMMON_FOOTER_TEXT")} -->
<div class="row">
    <div class="col-1 col-md"></div>
    <div class="col-4 col-md">
        <small class="d-block mb-12 text-muted" style="white-space: nowrap;"> <a href="http://www.semint.it/"> ${msg.get("PROGETTO")} SemInt <img width="50px" src="img/semint.png"></a> - <a href="https://www.inf.unibz.it/krdb/">KRDB Group <img width="50px" src="/img/krdb.png"></a> - <a href="https://www.unibz.it/">University of Bolzano <img width="50px" src="/img/unibz.jpg"></a></small>
    </div>
    <div class="col-1 col-md"></div>
</div>
</footer>
        </body>
        </html>
