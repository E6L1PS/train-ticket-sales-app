instance = new Mongo("mongodb-node-1:27017");
db = instance.getDB("traintickets");
rsconf = {
    _id: "rs0",
    members: [
        { _id: 0, host: "mongodb-node-1:27017" },
        { _id: 1, host: "mongodb-node-2:27017" },
        { _id: 2, host: "mongodb-node-3:27017" }
    ]
}

try {
    rs.conf();
}
catch {
    rs.initiate(rsconf);
}


