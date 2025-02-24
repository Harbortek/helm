import user from './user'
import org from './org'
import role from './role'
import param from './param'
import Enum from './enum'
import system from './system'
import log from './log'

export default {
    ...user,
    ...org,
    ...role,
    ...param,
    ...log,
    ...Enum,
    ...system,
}
