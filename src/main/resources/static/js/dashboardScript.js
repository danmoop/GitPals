function submitChanges() // This is executed when I press submit button in my form
{
    var user_name = document.getElementById('userName').innerHTML;
    document.getElementById('userNameInput').value = user_name;
}