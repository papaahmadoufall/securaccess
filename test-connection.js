// Simple Node.js script to test database connection
const mysql = require('mysql2');

const connection = mysql.createConnection({
  host: 'localhost',
  port: 3309,
  user: 'root',
  password: '',
  database: 'securaccess_db'
});

console.log('Connecting to MySQL database...');

connection.connect((err) => {
  if (err) {
    console.error('❌ Database connection failed:', err.message);
    return;
  }
  
  console.log('✅ Connected to MySQL database successfully!');
  
  // Test creating a simple table
  connection.query(`
    CREATE TABLE IF NOT EXISTS test_connection (
      id INT AUTO_INCREMENT PRIMARY KEY,
      message VARCHAR(255),
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    )
  `, (err) => {
    if (err) {
      console.error('❌ Table creation failed:', err.message);
    } else {
      console.log('✅ Test table created successfully!');
      
      // Insert test data
      connection.query(
        'INSERT INTO test_connection (message) VALUES (?)',
        ['SecurAccess Enterprise database is connected!'],
        (err, results) => {
          if (err) {
            console.error('❌ Insert failed:', err.message);
          } else {
            console.log('✅ Test data inserted successfully!');
            console.log('Database is ready for the SecurAccess Enterprise application.');
          }
          connection.end();
        }
      );
    }
  });
});