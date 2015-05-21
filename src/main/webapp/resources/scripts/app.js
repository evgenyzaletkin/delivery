$(document).ready(function() {
    var validate = function() {
        if (validateVolume()) {
            $('#submit').removeAttr('disabled');
        } else {
            $('#submit').attr('disabled','disabled');
        }
        validateShifts();
    };

    var validateVolume = function() {
        var rows = $("#assigned").children();
        var sum = 0;
        for (var i = 0; i < rows.length; i++) {
            var cells = $(rows[i]).children();
            sum += parseFloat($(cells[3]).text());
        }
        $("#total").text(sum);
        if (sum > 1400.0) {
            $ ("#errors").show();
            return false;
        } else {
            $ ("#errors").hide();
            return true;
        }
    };

    var validateShifts = function() {
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

    $('#up').click(function() {
        var checkboxes = $("#unassigned input:checked");
        for (var i = 0; i < checkboxes.length; i++) {
            var checkbox = $(checkboxes[i]);
            checkbox.attr("checked", false);
            var row = checkbox.parent().parent();
            row.detach();
            row.appendTo("#assigned");
        }
        validate();
    });

    $('#down').click(function() {
        var checkboxes = $("#assigned input:checked");
        for (var i = 0; i < checkboxes.length; i++) {
            var checkbox = $(checkboxes[i]);
            checkbox.attr("checked", false);
            var row = checkbox.parent().parent();
            row.detach();
            row.appendTo("#unassigned");
        }
        validate();
    });

    validate();

});
    
