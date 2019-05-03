window.onload = function()
{
    var i = 0;

    var colors = ["#16a085", "#c0392b", "#2c3e50", "#2980b9", "#8e44ad", "#f39c12"];

    setInterval(function() {
        document.getElementById("donateBtn").style.backgroundColor = colors[i];

        i++;

        if(i == 6) i = 0;
    }, 500);
}

function showSortForm()
{
    $("#sortList").toggle();
}