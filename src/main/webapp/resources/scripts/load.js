$(document).ready(function () {
    var validate = function () {
        var dlCheck = validateVolume(".total_dl", "#total_dl");
        var ulCheck = validateVolume(".total_ul", "#total_ul");
        if (dlCheck && ulCheck) {
            $('#submit').removeAttr('disabled');
            $("#errors").hide();
        } else {
            $('#submit').attr('disabled', 'disabled');
            $("#errors").show();
        }
        validateShifts();
    };

    var validateVolume = function (totalClass, elementId) {
        var cells = $("#assigned").find(totalClass);
        var sum = 0;
        for (var i = 0; i < cells.length; i++) {
            sum += parseFloat($(cells[i]).text());
        }
        $(elementId).text(sum);
        return sum <= 1400;
    };

    var validateShifts = function () {
        var shift = $('#shift').val();
        var rows = $("#assigned").children();
        var warnsPresented = false;
        for (var i = 0; i < rows.length; i++) {
            var row = $(rows[i]);
            var cells = row.children();
            var currentShift = $(cells[2]).text();
            if (currentShift != "" && currentShift != shift) {
                warnsPresented = true;
                row.addClass("warning");
            } else {
                row.removeClass("warning");
            }
        }
        var warns = $("#warns");
        if (warnsPresented) warns.show();
        else warns.hide();
        return true;
    };

    $('#up').click(function () {
        moveRow("#unassigned", "#assigned");
        validate();
    });

    $('#down').click(function () {
        moveRow("#assigned", "#unassigned");
        validate();
    });

    $('#next').click(function () {
        redirect("next");
    });

    $('#previous').click(function () {
        redirect("previous");
    });

    var redirect = function(param) {
        window.location.href = window.location.href + "&" + param + "=" + param;
    };

    var moveRow = function (from, to) {
        var checkboxes = $(from).find("input:checked");
        for (var i = 0; i < checkboxes.length; i++) {
            var checkbox = $(checkboxes[i]);
            checkbox.attr("checked", false);
            var row = checkbox.parent().parent();
            row.removeClass("warning");
            processRow(row, from, to);
        }
    };

    var processRow = function (row, from, to) {
        var itemsNum = row.find(".items").children("input").val();
        if (itemsNum !== 0) {
            var allItemsNum = row.find(".allitems").text();
            var orderNumber = row.attr("class");
            var allRows = $("." + orderNumber);
            var toRow = $(to).find(allRows);
            console.log(toRow.length === 0);
            console.log(itemsNum + " " + allItemsNum);
            if (toRow.length === 0 && itemsNum === allItemsNum) {
                row.appendTo($(to));
            } else if (toRow.length !== 0) {
                var toAllItems = toRow.find(".allitems");
                var toItems = toRow.find(".items").children("input");
                var toAllItemsNum = toAllItems.text();
                toAllItems.text(parseInt(toAllItemsNum) + parseInt(itemsNum));
                toItems.val(parseInt(toAllItemsNum) + parseInt(itemsNum));
                calculateVolume(toRow);
                if (allItemsNum === itemsNum) {
                    row.remove();
                } else {
                    var newItems = allItemsNum - itemsNum;
                    row.find(".items").children("input").val(newItems);
                    row.find(".allitems").text(newItems);
                    calculateVolume(row);
                }
            } else {
                toRow = row.clone();
                toRow.find(".items").children("input").val(itemsNum);
                toRow.find(".allitems").text(itemsNum);
                calculateVolume(toRow);
                toRow.appendTo(to);

                var newItems = allItemsNum - itemsNum;
                row.find(".items").children("input").val(newItems);
                row.find(".allitems").text(newItems);
                calculateVolume(row);
            }
        }
    };

    var calculateVolume = function (row) {
        var vol = row.find(".single").text();
        var items = row.find(".allitems").text();
        var total = parseInt(items) * parseFloat(vol);
        row.find(".total").text(total);
    };
    validate();

});
    
