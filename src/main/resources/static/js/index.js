document.getElementById('card-block-toggler').addEventListener('click', () => {
    var element = document.getElementById('card-block');

    var style = element.style.display == "none" ? "block" : "none";

    element.style.display = style;
});