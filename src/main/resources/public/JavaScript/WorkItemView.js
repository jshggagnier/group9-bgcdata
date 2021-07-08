function parseTeamString(team){
    var i = 0;
    team = team.split("|");
    while(i < team.length){
    team[i] = team[i].split(",");
    var Team = "";
    var c = 0;
    team[i].forEach(t => {
        if(c == 0){
            Team += t.slice(3)+" ";
            c++;
        }else{
        Team += t+" ";
        }
    });
    console.log(Team);
    console.log(team);
    i++;
}
}