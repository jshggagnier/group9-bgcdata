function parseTeamString(){
var table = document.getElementById("WorkItemViewtable");
var rows = table.rows;
var frow = false;
for(var j = 0; j < rows.length; j++){
    if(frow){
var i = 0;
var team = rows[j].cells[3].innerHTML;
//console.log(team);
    team = team.split("|");
var Team = "<table>";
    while(i < team.length){
        if(i != (team.length-1)){
    Team += "<tr>"
    team[i] = team[i].split(",");
    var c = 0;
    //console.log(team);
    team[i].forEach(t => {
        if(c == 0){
            Team += "<td>"+t.slice(3)+"</td> ";
            c++;
        }else{
        Team += "<td>"+t+"</td>";
        }
    });
    Team += "</tr>"
}
i++;
}
//console.log(Team);
rows[j].cells[3].innerHTML = Team+"</table>";
}else{
    frow = true;
}
}
}