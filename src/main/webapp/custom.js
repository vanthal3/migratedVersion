function addEvents() {
      var group = document.getElementsByClassName("LinkedList");
      for (var i = 0; i < group.length; i++){
          activateTree(group[i]);
      }
      //activateTree(document.getElementById("LinkedList1"));
    }

    // This function traverses the list and add links
    // to nested list items
    function activateTree(oList) {
      // Collapse the tree
      for (var i=0; i < oList.getElementsByTagName("ul").length; i++) {
        oList.getElementsByTagName("ul")[i].style.display="none";
      }
      // Add the click-event handler to the list items
      if (oList.addEventListener) {
        oList.addEventListener("click", toggleBranch, false);
      } else if (oList.attachEvent) { // For IE
        oList.attachEvent("onclick", toggleBranch);
      }
      // Make the nested items look like links
      //addLinksToBranches(oList);
    }

    // This is the click-event handler
    function toggleBranch(event) {
      var oBranch, cSubBranches;
      if (event.target) {
        oBranch = event.target;
      } else if (event.srcElement) { // For IE
        oBranch = event.srcElement;
      }
      cSubBranches = oBranch.getElementsByTagName("ul");
      if (cSubBranches.length > 0) {
        if (cSubBranches[0].style.display == "block") {
          cSubBranches[0].style.display = "none";
        } else {
          cSubBranches[0].style.display = "block";
        }
      }
    }

    // This function makes nested list items look like links
//    function addLinksToBranches(oList) {
//      var cBranches = oList.getElementsByTagName("li");
//      var i, n, cSubBranches;
//      if (cBranches.length > 0) {
//        for (i=0, n = cBranches.length; i < n; i++) {
//          cSubBranches = cBranches[i].getElementsByTagName("ul");
//          if (cSubBranches.length > 0) {
//            addLinksToBranches(cSubBranches[0]);
//            cBranches[i].className = "HandCursorStyle";
//            console.log(cBranches[i].innerHTML);
////            cBranches[i].style.color = "blue";
////            cSubBranches[0].style.color = "black";
////            cSubBranches[0].style.cursor = "auto";
//          }
//        }
//      }
//    }

//    function makeRed() {
//            TDs = document.getElementsByTagName('td');
//            for (var i=0; i<TDs.length; i++) {
//                    var temp = TDs[i];
//                    if (temp.firstChild.nodeValue ==) temp.className = "negative";
//                }
//        }
