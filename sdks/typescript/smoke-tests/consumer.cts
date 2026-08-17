import sdk = require("@cregis/sdk");

const client: sdk.CregisTeamClient = new sdk.CregisTeamClient({
  baseUrl: "https://sandbox.example",
  accessKey: "access-key",
  accessSecret: "access-secret",
});

void client.listTeamWallets({ page_num: 1, page_size: 10 });
