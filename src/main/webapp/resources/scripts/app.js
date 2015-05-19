$(document).ready(function() {
    var validate = function() {
        var isValid = validateVolume() && validateShifts();
        if (isValid) {
            $('#submit').removeAttr('disabled');
        } else {
            $('#submit').attr('disabled','disabled');
        }
    };

    var validateVolume = function() {
        var rows = $("#assigned").children();
        var sum = 0;
        for (var i = 0; i < rows.length; i++) {
            var cells = $(rows[i]).children();
            sum += parseInt($(cells[3]).text());
        }
        console.log(sum);
        $("#volume").remove();
        if (sum > 50) {
            $ ("#errors").show();
            $('<p id="volume">').text("Volume is more than allowed volume").appendTo("#errors");
            return false;
        } else {
            $ ("#errors").hide();
            $("#volume").remove();
            return true;
        }
    };

    var validateShifts = function() {
        $ ("#warns").hide();
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
});
    
