function setup(email,role)
{
    document.getElementById("email").value = email;
    document.getElementById("role").value = role;
    document.getElementById("displaymail").innerHTML = email;
    if(role == "admin") {document.getElementById("admin").checked = true;}
    else if(role == "viewedit") {document.getElementById("ViewEdit").checked = true;}
    else if(role == "viewonly") {document.getElementById("ViewOnly").checked = true;}
    else {document.getElementById("unverified").checked = true;}
}

function modifyfields()
{
    if (document.getElementById("admin").checked == true) {document.getElementById("role").value = "admin"}
    else if(document.getElementById("ViewEdit").checked == true) {document.getElementById("role").value = "viewedit"}
    else if(document.getElementById("ViewOnly").checked == true) {document.getElementById("role").value = "viewonly"}
    else {document.getElementById("role").value = "unverified"}
}