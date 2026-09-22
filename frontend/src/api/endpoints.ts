import { http } from './http'
import type { Page, User, WeeklyStats, Workout, WorkoutDetails } from './types'

export interface Credentials {
  email: string
  password: string
}

export interface Registration extends Credentials {
  displayName: string
}

export interface ProfileUpdate {
  displayName: string
  weightKg: number
  weeklyGoalMinutes: number
}

export const authApi = {
  register: (registration: Registration) => http.post<User>('/api/auth/register', registration),
  login: (credentials: Credentials) => http.post<User>('/api/auth/login', credentials),
  logout: () => http.post<void>('/api/auth/logout'),
}

export const profileApi = {
  me: () => http.get<User>('/api/me'),
  update: (update: ProfileUpdate) => http.put<User>('/api/me', update),
}

export const workoutApi = {
  list: (page = 0, size = 10) => http.get<Page<Workout>>(`/api/workouts?page=${page}&size=${size}`),
  create: (details: WorkoutDetails) => http.post<Workout>('/api/workouts', details),
  update: (id: number, details: WorkoutDetails) => http.put<Workout>(`/api/workouts/${id}`, details),
  remove: (id: number) => http.delete(`/api/workouts/${id}`),
}

export const statsApi = {
  week: () => http.get<WeeklyStats>('/api/stats/week'),
}
