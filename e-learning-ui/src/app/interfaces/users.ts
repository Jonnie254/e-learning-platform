 export interface Role {
  STUDENT: string;
  INSTRUCTOR: string;
  ADMIN: string;
}


 export enum RoleRequestStatus {
   PENDING = 'PENDING',
   APPROVED = 'APPROVED',
   REJECTED = 'REJECTED'
 }


export interface User {
  id?: string;
  firstName: string;
  lastName: string;
  password?: string;
  email: string;
  profilePicUrl?: string;
  role?: string;
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
  email: string;
  password?: string;
  role: string;
  profilePicUrl?: string;
}
export interface AuthResponse {
  token: string;
 }

export interface RoleResponse {
   id: string;
   role: Role;
   status: RoleRequestStatus;
   userEmail: string;
   userId: string;
 }
