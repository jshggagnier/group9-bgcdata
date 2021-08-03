function parseTeamString(){
var table = document.getElementById("WorkItemViewtable");
var rows = table.rows;
var StartDate = document.getElementById("startdate").valueAsDate;
var EndDate = document.getElementById("enddate").valueAsDate;
var weeks = weeksBetween(StartDate, EndDate);
for(var j = 1; j < rows.length; j++){
var team = rows[j].cells[3].innerHTML;
//console.log(team);
createTable(team, rows, weeks, StartDate);
}
}

function weeksBetween(StartDate, EndDate) {
    return Math.ceil((EndDate - StartDate) / (7 * 24 * 60 * 60 * 1000));//rounds up the amount of weeks between the two dates (the number is the amount of milliseconds in a week)
}

function createTable(team, rows, weeks, startdate){
    var i = 0;
    team = team.split("|");
    var Team = "<table> <tr><th>Team Name</th><th>Type</th>";
    
    for(var j = 1; j <= weeks; j++){
        var d = startdate.getTime()+((j)*(7 * 24 * 60 * 60 * 1000) + (24 * 60 * 60 * 1000));
        console.log(d);
        var daterow = new Date(d);
        Team += "<th>"+daterow.toString().substring(0, 15)+"</th>";
    }
    Team += "</tr>"
        while(i < team.length){
            if(i != (team.length-1)){
        Team += "<tr>"
        team[i] = team[i].split(",");
        var c = 0;
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

    /*
    if(weeks < 1) {weeks = 1;}
    //
    var TableRows = weeks+2;
    var currentrowpntr;
    var cellcounter = 0;
    var currentcellpntr;
    for(var RowCounter = 0;RowCounter < TableRows; RowCounter++)
    {
        
        currentrowpntr = document.getElementById("Row"+RowCounter);
        cellcounter = currentrowpntr.cells.length;
        if(RowCounter == 0){
            var temp = cellcounter-1;
            while(temp > 0){currentrowpntr.deleteCell(temp);temp--}
            cellcounter = 1;
        }
        while (cellcounter != (weeks+2))
        {
            //console.log(cellcounter + "/" + weeks);
            if (cellcounter < (weeks+2)) {
                currentcellpntr = currentrowpntr.insertCell(-1);
                if(RowCounter == 0)
                {
                    var Cheese = new Date(StartDate.getTime() + ((cellcounter-1)*(7 * 24 * 60 * 60 * 1000) + (24 * 60 * 60 * 1000)));
                    //maybe concatenate calculation here
                    if(cellcounter == 1){
                        currentcellpntr.innerHTML = "Item Type";
                        currentcellpntr.className = "itemheader";
                        }else{
                        currentcellpntr.innerHTML = Cheese.toString().substring(0,15);
                        currentcellpntr.className = "itemheader";
                        }
                }
                else
                {
                    if (cellcounter == 0)
                    {
                        currentcellpntr.innerHTML = "<input type='text' id='"+("cell"+RowCounter+":"+cellcounter)+"' required><br><button type='button' class='delbtn' id='btn"+RowCounter+"' onclick='deleterow("+RowCounter+")'>Delete This Row</button>";
                        currentcellpntr.className = "rowname";
                    }
                    else if (cellcounter == 1){
                        currentcellpntr.innerHTML = '<td><select id="cell'+RowCounter+":"+cellcounter+'"><option value="Dev">Dev</option><option value="QA">QA</option></select></td>';
                        currentcellpntr.className = "rowname";
                    }
                    else {currentcellpntr.innerHTML = "<input type='number' id='"+("cell"+RowCounter+":"+cellcounter)+"'  min='0' >";}
                }
            }
            else if (cellcounter > (weeks+2)) {
                currentrowpntr.deleteCell(-1);
            }
            cellcounter = currentrowpntr.cells.length;
        }
    } 
    */
}