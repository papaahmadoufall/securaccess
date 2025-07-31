// Senegal Formatting Utilities

// Phone number formatting for Senegal (+221)
export function formatSenegalPhone(phone) {
  if (!phone) return '';
  const cleaned = phone.replace(/\D/g, '');
  
  // Senegal mobile numbers are 9 digits
  if (cleaned.length === 9) {
    return cleaned.replace(/(\d{2})(\d{3})(\d{2})(\d{2})/, '$1 $2 $3 $4');
  }
  return phone;
}

// Validate Senegal phone number
export function isValidSenegalPhone(phone) {
  const cleaned = phone.replace(/\D/g, '');
  // Senegal mobile prefixes: 77, 78, 70, 76
  return cleaned.length === 9 && /^(77|78|70|76)/.test(cleaned);
}

// Date formatting for French Senegal
export function formatSenegalDate(date) {
  return new Date(date).toLocaleDateString('fr-SN', {
    day: '2-digit',
    month: '2-digit', 
    year: 'numeric'
  });
}

// DateTime formatting for French Senegal
export function formatSenegalDateTime(date) {
  return new Date(date).toLocaleString('fr-SN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false
  });
}

// Time formatting for French Senegal
export function formatSenegalTime(date) {
  return new Date(date).toLocaleTimeString('fr-SN', {
    hour: '2-digit',
    minute: '2-digit',
    hour12: false
  });
}

// Currency formatting for West African CFA franc
export function formatCFA(amount) {
  return new Intl.NumberFormat('fr-SN', {
    style: 'currency',
    currency: 'XOF',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  }).format(amount);
}

// Format phone number as user types (for input fields)
export function formatPhoneInput(phone) {
  // Remove all non-digits
  let cleaned = phone.replace(/\D/g, '');
  
  // Limit to 9 digits (Senegal mobile format)
  if (cleaned.length > 9) {
    cleaned = cleaned.substring(0, 9);
  }
  
  // Format as 77 123 45 67 (Senegal format)
  if (cleaned.length >= 2) {
    cleaned = cleaned.replace(/(\d{2})(\d{3})?(\d{2})?(\d{2})?/, 
      (match, p1, p2, p3, p4) => {
        let formatted = p1;
        if (p2) formatted += ' ' + p2;
        if (p3) formatted += ' ' + p3;
        if (p4) formatted += ' ' + p4;
        return formatted;
      });
  }
  
  return cleaned;
}

// Get international format for phone
export function getInternationalPhone(phone) {
  const cleaned = phone.replace(/\D/g, '');
  if (cleaned.length === 9) {
    return `+221 ${formatSenegalPhone(cleaned)}`;
  }
  return phone;
}