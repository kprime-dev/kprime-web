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
<body>
<header>

    <div class="row kprime-panel">
        <div class="col-1 col-md">
            <a class="navbar-brand" href="/index"><img id="logo" class="logo-banner" src="/img/kprime-logo-bw.png" alt="KPrime"></a>
        </div>
        <div class="col-4 col-md">
            <#if avatarUrl??>
            <img class="border border-primary rounded-circle avatar" src="${avatarUrl}">
            </#if>
            <#if currentUser??>
            &nbsp; <a href="/users.html">${currentUser}</a>
            </#if>
        </div>
        <div class="col-5 col-md">
            <a href="/events.html">events</a>
        </div>
        <div class="col-1 col-md">
            <a href="/help/index.html" target="help"><img width="40px" src="/img/help.png" /></a>
        </div>
        <div class="col-1 col-md">
            <form method="post" action="/logout" class="form-inline">
                <button class="btn btn-success my-2 my-lg-0" id="logout"><img class="icon_logout" src="img/exit.jpg" alt="exit"></button>
            </form>
        </div>
    </div>



            <!--
                <form>
                    <div>
                        <a href="/locale/en"><img width="20px" src="/img/flag_en.png"></a>
                    </div>
                    <div>
                        <a href="/locale/zh"><img width="20px" src="/img/flag_zh.png"></a>
                    </div>
                </form>
            -->

</header>
<hr/>
<main>
    <div id="content">
