const WebSocket = require('ws');

const wss = new WebSocket.Server({ port: 3001 });

wss.on('connection', function connection(ws) {
  console.log('WebSocket connected');

  // 发送欢迎消息
  ws.send(
    JSON.stringify({
      type: 'welcome',
      message: 'WebSocket connection established',
    })
  );

  // 监听消息
  ws.on('message', function incoming(message) {
    console.log('received: %s', message);

    // 回显消息
    ws.send(
      JSON.stringify({
        type: 'echo',
        data: JSON.parse(message),
      })
    );
  });

  // 定期发送心跳
  const heartbeat = setInterval(() => {
    if (ws.readyState === WebSocket.OPEN) {
      ws.send(
        JSON.stringify({
          type: 'heartbeat',
          timestamp: Date.now(),
        })
      );
    }
  }, 30000);

  ws.on('close', () => {
    clearInterval(heartbeat);
    console.log('WebSocket disconnected');
  });
});

console.log('WebSocket server running on ws://localhost:3001');
