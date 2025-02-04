 export  interface Role {
  STUDENT: string;
  INSTRUCTOR: string;
  ADMIN: string;
}

export interface User {
      id: string;
      firstName: string;
      lastName: string;
       password: string;
      email: string;
      profilePicUrl?: string;
      role: string;
}

export interface registerUser{
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  confirmPassword: string;
}

export interface loginUser{
  email: string;
  password: string;
}

export interface UserDetailsResponse {
  id: string;
  firstName: string;
  lastName: string;
  password: string;
  email: string;
  role: string;
}
export interface AuthResponse {
  token: string;
 }
