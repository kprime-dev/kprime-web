<#include "/freemarker/layout_header.ftl">

<div class="container-fluid">

<#if currentProject??>
<div class="kprime-panel">
    <h3>Context <b>${currentProject}</b>  <a href="/admin.html" class="btn btn-success">Manage</a>
        <#if currentProject!=""><a href="/project/${currentProject}/publish" class="btn btn-success">Publish</a></#if>
    </h3>
    <!--<p>folder: <b>${currentTrace}</b></p>-->
</div>
</#if>

<#if currentProject!="">
<div class="row">

    <div class="col">
        <div class="card " style="margin-bottom:20px;">
            <img src="img/todo-ok.png" class="card-img-top card-img-index" alt="...">
            <div class="card-body card-img-overlay">
                <h5 class="card-title">${msg.get("HOME_BUTTON_GOALS")}</h5>
                <p class="card-text">to model ${currentProject} motivations</p>
                <a href="/todos.html" class="btn btn-success">Manage</a>
            </div>
        </div>
    </div>

    <div class="col">
        <div class="card" style="margin-bottom:20px;">
            <img src="img/obiettivo.png" class="card-img-top card-img-index" alt="...">
            <div class="card-body card-img-overlay">
                <h5 class="card-title">Stories  </h5>
                <p class="card-text">to model ${currentProject} facts</p>
                <a href="/noteedit.html" accesskey="h" class="btn btn-success">Manage</a>
            </div>
        </div>
    </div>


    <div class="col">
        <div class="card " style="margin-bottom:20px;">
            <img src="img/linked-data.png" class="card-img-top card-img-index" alt="...">
            <div class="card-body card-img-overlay">
                <h5 class="card-title">Terms </h5>
                <p class="card-text">to model ${currentProject} vocabulary</p>
                <a href="/dictionary.html" class="btn btn-success">Manage</a>
            </div>
        </div>
    </div>

</div>
</#if>


<#include "/freemarker/layout_footer.ftl">
