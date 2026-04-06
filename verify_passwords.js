const bcrypt = require('bcryptjs');

// Hashes fetched from the live Supabase DB
const tests = [
  {
    label:    'HOD001   → password: 01011980@hod',
    plain:    '01011980@hod',
    hash:     '$2a$10$vvvn716ObyhWpG3zMY3NYuazabI6SnlhE1dRdbIGcDci7eKEntEXa'
  },
  {
    label:    'CC001    → password: 01011985@cc',
    plain:    '01011985@cc',
    hash:     '$2a$10$Hq/l.bfcTbOk2nMpLtJ6ae0kPVPe5OotSh6me43eCP2jCxbnRUwCi'
  },
  {
    label:    'STF001   → password: 01011990@staff',
    plain:    '01011990@staff',
    hash:     '$2a$10$ZGJfeZB8K7stdYwHsg/wpemc5V5Qk/Q9MN6kwgtBtvMIkriVeiqHK'
  },
  {
    label:    'Student1 → password: 01012003@student',
    plain:    '01012003@student',
    hash:     '$2a$10$S4D1GbNL5MJ2zVhgU0Sg5ePU1yPyxMJWc/wgGU8ihCrnxb9te4ZGy'
  },
  // Also test wrong passwords to be thorough
  {
    label:    'HOD001   → wrong password: password123',
    plain:    'password123',
    hash:     '$2a$10$vvvn716ObyhWpG3zMY3NYuazabI6SnlhE1dRdbIGcDci7eKEntEXa'
  }
];

console.log('=== BCrypt Password Verification ===\n');
for (const t of tests) {
  const ok = bcrypt.compareSync(t.plain, t.hash);
  console.log(`${ok ? '✅' : '❌'}  ${t.label}`);
}
