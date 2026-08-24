function navbar() {
    return `
    <div class="navbar">
        <a href="../index.html"><img class="homebutton" src="../imgs/SimpleLogo.png"></a>
        <a href="../downloadpage/index.html" class="selectionbuttons">Downloads</a>
        <div class="selectionbuttons">Changelog</div>
        <div class="selectionbuttons">Manuals</div>
        <div class="selectionbuttons">About</div>
        <div class="selectionbuttons">Github</div>
        <img class="tabbutton" src="../imgs/bars-solid-full.svg">
    </div>
    `;
}

function addrow(ver, date, desc) {
    return `<div class="row">
                <div class="cellVer">
                    ${ver}
                </div>
                <div class="cellDate">
                    ${date}
                </div>
                <div class="cellDesc">
                    ${desc}
                </div>
            </div>`;
}