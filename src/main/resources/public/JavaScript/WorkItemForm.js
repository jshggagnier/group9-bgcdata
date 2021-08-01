
function AddTeam(){
    var table = document.getElementById("InputTable");
    var TableRows = (table.rows.length-1);
    var newrow = table.insertRow(-1);
    newrow.id = "Row"+(TableRows+1);
    UpdateTable();
}

function DeleteTeam(){
    var table = document.getElementById("InputTable");
    var TableRows = (table.rows.length-1);
    if(TableRows == 1){alert("Must have at least one Team");}
    else
    {
        table.deleteRow(-1);
    }
}

function weeksBetween(StartDate, EndDate) {
    return Math.ceil((EndDate - StartDate) / (7 * 24 * 60 * 60 * 1000));//rounds up the amount of weeks between the two dates (the number is the amount of milliseconds in a week)
}

function UpdateTable(){
    //console.log("Updating Table");
    var table = document.getElementById("InputTable");
    var StartDate = document.getElementById("startDate").valueAsDate;
    var EndDate = document.getElementById("endDate").valueAsDate;
    if(EndDate < StartDate) 
    {
        document.getElementById("endDate").valueAsDate = StartDate;
    }
    var weeks = weeksBetween(StartDate,EndDate);
    if(weeks < 1) {weeks = 1;}
    var TableRows = (table.rows.length);
    var currentrowpntr;
    var cellcounter = 0;
    var currentcellpntr;
    for(var RowCounter = 0;RowCounter < TableRows;RowCounter++)
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
    createTeamString();
}

function createTeamString(){
    var table = document.getElementById("InputTable");
    var TableRows = (table.rows.length);
    var TableCells = document.getElementById("Row0").cells.length;
    var TableString = "";
    //console.log(TableRows+"|I|"+TableCells);
    for(var RowCounter = 1;RowCounter < TableRows;RowCounter++)
    {
        TableString += "T"+RowCounter+":"
        for(var CellCounter = 0; CellCounter < TableCells; CellCounter++)
        {
            if(document.getElementById("cell"+RowCounter+":"+CellCounter).value != ""){
            TableString += document.getElementById("cell"+RowCounter+":"+CellCounter).value;
            }else{
            TableString += "0";
            }
            if(CellCounter != TableCells-1){
              TableString += ","
            }
        }
        TableString += "|";
    }
    //console.log(TableString);
    document.getElementById("TBstring").value = TableString;
}

function deleterow(row)
{
    console.log("deleterow"+row);
    var table = document.getElementById("InputTable");
    var StartDate = document.getElementById("startDate").valueAsDate;
    var EndDate = document.getElementById("endDate").valueAsDate;
    if(EndDate < StartDate) 
    {
        document.getElementById("endDate").valueAsDate = StartDate;
    }
    var TableRows = (table.rows.length-1);
    var target;
    var cellupdate = 0;
    var cellupdatepnt;
    if(TableRows == 1){alert("Must have at least one Team");}
    else
    {
        table.deleteRow(row);
        while (row < TableRows)
        {
            console.log(row+",");
            target = document.getElementById("Row"+(row+1));
            cellupdatepnt = document.getElementById("btn"+(row+1));
            cellupdatepnt.setAttribute("onclick", "deleterow("+row+");");
            console.log(cellupdatepnt.onclick);
            cellupdatepnt.id = "btn"+row;
            while(cellupdate < target.cells.length)
            {
                cellupdatepnt = document.getElementById("cell"+(row+1)+":"+cellupdate);
                cellupdatepnt.id = "cell"+row+":"+cellupdate;
                cellupdate++;
            }
            target.id = "Row"+row;
            row++;
            cellupdate = 0;
        }
    }
    createTeamString();
}
