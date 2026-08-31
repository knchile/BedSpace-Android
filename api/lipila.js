// Vercel Serverless Function to proxy Lipila Payment Gateway requests securely without CORS issues
export default async function handler(req, res) {
  // Set CORS headers so any client can call it
  res.setHeader('Access-Control-Allow-Credentials', 'true');
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET,OPTIONS,PATCH,DELETE,POST,PUT');
  res.setHeader(
    'Access-Control-Allow-Headers',
    'X-CSRF-Token, X-Requested-With, Accept, Accept-Version, Content-Length, Content-MD5, Content-Type, Date, X-Api-Version, x-api-key'
  );

  if (req.method === 'OPTIONS') {
    res.status(200).end();
    return;
  }

  if (req.method !== 'POST') {
    return res.status(405).json({ message: 'Method Not Allowed' });
  }

  const apiKey = req.headers['x-api-key'] || process.env.LIPILA_API_KEY || 'lsk_019f41c4-269e-7529-ab2d-c3a3b099e76f';
  const isCard = req.body && req.body.isCard;
  const targetUrl = isCard 
    ? 'https://blz.lipila.io/api/v1/collections/card'
    : 'https://blz.lipila.io/api/v1/collections/mobile-money';

  try {
    const lipilaResponse = await fetch(targetUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'x-api-key': apiKey
      },
      body: JSON.stringify(req.body)
    });

    const data = await lipilaResponse.json().catch(() => null);
    return res.status(lipilaResponse.status).json(data || { status: lipilaResponse.statusText });
  } catch (error) {
    console.error('Lipila Proxy Error:', error);
    return res.status(502).json({
      message: 'Failed to communicate with Lipila Gateway upstream',
      error: error.message
    });
  }
}
